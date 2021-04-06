package xyz.srclab.spring.boot.grpc.server

import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.getBeansWithAnnotation
import org.springframework.context.ApplicationContext
import xyz.srclab.common.collect.MutableSetMap
import xyz.srclab.common.collect.toImmutableMap
import xyz.srclab.common.collect.toMutableSetMap
import javax.annotation.Resource

open class DefaultGrpcServersFactory : GrpcServersFactory {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Resource
    private lateinit var grpcServerFactory: GrpcServerFactory

    override fun create(serversProperties: GrpcServersProperties): Map<String, Server> {
        val serviceGroups: MutableSetMap<String, BindableService> =
            mutableMapOf<String, MutableSet<BindableService>>().toMutableSetMap()
        val interceptorGroups: MutableSetMap<String, ServerInterceptor> =
            mutableMapOf<String, MutableSet<ServerInterceptor>>().toMutableSetMap()
        val processedBindableServices: MutableSet<String> = mutableSetOf()

        fun groupService(
            annotation: GrpcService?,
            beanName: String,
            bean: BindableService,
        ) {
            if (annotation === null) {
                logger.debug("find gRPC service $beanName in $DEFAULT_GROUP_NAME group.")
                serviceGroups.add(DEFAULT_GROUP_NAME, bean)
                return
            }
            val groups = annotation.valueOrGroup
            if (groups.isEmpty()) {
                logger.debug("find gRPC service $beanName in $DEFAULT_GROUP_NAME group.")
                serviceGroups.add(DEFAULT_GROUP_NAME, bean)
            } else {
                logger.debug("find gRPC service $beanName in groups ${groups.joinToString()}.")
                for (group in groups) {
                    serviceGroups.add(group, bean)
                }
            }
        }

        fun groupInterceptors(
            annotation: GrpcServerInterceptor?,
            beanName: String,
            bean: ServerInterceptor,
        ) {
            if (annotation === null) {
                logger.debug("find gRPC interceptor $beanName for all service groups.")
                interceptorGroups.add("", bean)
                return
            }
            val groupPatterns = annotation.valueOrGroupPattern
            if (groupPatterns.isEmpty()) {
                logger.debug("find gRPC interceptor $beanName for all service groups.")
                interceptorGroups.add("", bean)
            } else {
                logger.debug("find gRPC interceptor $beanName for service groups ${groupPatterns.joinToString()}.")
                for (groupPattern in groupPatterns) {
                    interceptorGroups.add(groupPattern, bean)
                }
            }
        }

        //find all BindableService
        val bindableServices = applicationContext.getBeansOfType(BindableService::class.java)
        for (serviceEntry in bindableServices) {
            val beanName = serviceEntry.key
            val bean = serviceEntry.value
            val serviceAnnotation = applicationContext.findAnnotationOnBean(beanName, GrpcService::class.java)
            groupService(serviceAnnotation, beanName, bean)
            processedBindableServices.add(beanName)
        }

        //find all @GrpcService
        val grpcServices = applicationContext.getBeansWithAnnotation<GrpcService>()
        for (serviceEntry in grpcServices) {
            val beanName = serviceEntry.key
            if (processedBindableServices.contains(beanName)) {
                continue
            }
            val bean = serviceEntry.value
            if (bean !is BindableService) {
                throw IllegalArgumentException(
                    "Type of gRPC service bean should be an ImplBase class. now it is ${bean.javaClass}"
                )
            }
            val serviceAnnotation = applicationContext.findAnnotationOnBean(beanName, GrpcService::class.java)
            groupService(serviceAnnotation, beanName, bean)
        }

        //find all server interceptor
        val serverInterceptors = applicationContext.getBeansOfType(ServerInterceptor::class.java)
        for (interceptorEntry in serverInterceptors) {
            val beanName = interceptorEntry.key
            val bean = interceptorEntry.value
            val interceptorAnnotation =
                applicationContext.findAnnotationOnBean(beanName, GrpcServerInterceptor::class.java)
            groupInterceptors(interceptorAnnotation, beanName, bean)
        }

        //build gRPC server
        val result: MutableMap<String, Server> = mutableMapOf()
        for (serverEntry in serversProperties.servers) {
            val serverName = serverEntry.key
            val serverProperties = serverEntry.value
            result[serverName] =
                grpcServerFactory.create(serverProperties, serversProperties, serviceGroups, interceptorGroups)
        }
        return result.toImmutableMap()
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(DefaultGrpcServersFactory::class.java)
    }
}