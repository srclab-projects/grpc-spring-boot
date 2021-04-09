package xyz.srclab.grpc.spring.boot.server

import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.util.AntPathMatcher
import xyz.srclab.common.collect.MutableSetMap
import xyz.srclab.common.collect.toImmutableMap
import xyz.srclab.common.collect.toMutableSetMap
import javax.annotation.Resource

open class DefaultGrpcServersFactory : GrpcServersFactory {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Resource
    private lateinit var grpcServerFactory: GrpcServerFactory

    private val antPathMatcher = AntPathMatcher()

    override fun create(serverDefinitions: Set<GrpcServerDefinition>): Map<String, Server> {
        val servers: MutableSetMap<String, GrpcServiceDefinitionBuilder> =
            mutableMapOf<String, MutableSet<GrpcServiceDefinitionBuilder>>().toMutableSetMap()

        fun BindableService.matchServers(beanName: String, serviceAnnotation: GrpcService?) {
            if (serviceAnnotation === null || serviceAnnotation.valueOrServerPatterns.isEmpty()) {
                for (serverDefinition in serverDefinitions) {
                    servers.add(
                        serverDefinition.name,
                        GrpcServiceDefinitionBuilder.newGrpcServiceDefinitionBuilder(beanName, this, null)
                    )
                }
                return
            }
            for (serverPattern in serviceAnnotation.valueOrServerPatterns) {
                for (serverDefinition in serverDefinitions) {
                    if (antPathMatcher.match(serverPattern, serverDefinition.name)) {
                        servers.add(
                            serverDefinition.name,
                            GrpcServiceDefinitionBuilder.newGrpcServiceDefinitionBuilder(beanName, this, null)
                        )
                    }
                }
            }
        }

        fun ServerInterceptor.matchServices(interceptorAnnotation: GrpcServerInterceptor?) {
            if (interceptorAnnotation === null || interceptorAnnotation.valueOrServicePatterns.isEmpty()) {
                for (serverEntry in servers) {
                    val services = serverEntry.value
                    for (service in services) {
                        service.addInterceptor(this)
                    }
                }
                return
            }
            for (valueOrServicePattern in interceptorAnnotation.valueOrServicePatterns) {
                for (serverEntry in servers) {
                    val services = serverEntry.value
                    for (service in services) {
                        if (antPathMatcher.match(valueOrServicePattern, service.beanName)) {
                            service.addInterceptor(this)
                        }
                    }
                }
            }
        }

        //group BindableServices
        val bindableServices = applicationContext.getBeansOfType(BindableService::class.java)
        for (serviceEntry in bindableServices) {
            val beanName = serviceEntry.key
            val bean = serviceEntry.value
            val serviceAnnotation = applicationContext.findAnnotationOnBean(beanName, GrpcService::class.java)
            logger.debug("Load gRPC service: $beanName (${bean.javaClass}).")
            bean.matchServers(beanName, serviceAnnotation)
        }

        //add interceptors
        val serverInterceptors = applicationContext.getBeansOfType(ServerInterceptor::class.java)
        for (interceptorEntry in serverInterceptors) {
            val beanName = interceptorEntry.key
            val bean = interceptorEntry.value
            val interceptorAnnotation =
                applicationContext.findAnnotationOnBean(beanName, GrpcServerInterceptor::class.java)
            logger.debug("Load gRPC server interceptor: $beanName (${bean.javaClass}).")
            bean.matchServices(interceptorAnnotation)
        }

        //build gRPC server
        val result: MutableMap<String, Server> = mutableMapOf()
        for (serverDefinition in serverDefinitions) {
            val serviceBuilders = servers[serverDefinition.name]
            if (serviceBuilders === null) {
                continue
            }
            result[serverDefinition.name] = grpcServerFactory.create(serverDefinition, serviceBuilders)
        }
        return result.toImmutableMap()
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(DefaultGrpcServersFactory::class.java)
    }
}