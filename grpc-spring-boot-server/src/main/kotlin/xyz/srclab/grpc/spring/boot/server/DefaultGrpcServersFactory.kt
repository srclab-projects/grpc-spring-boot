package xyz.srclab.grpc.spring.boot.server

import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.util.AntPathMatcher
import xyz.srclab.common.collect.MutableSetMap
import xyz.srclab.common.collect.MutableSetMap.Companion.toMutableSetMap
import xyz.srclab.common.collect.map
import xyz.srclab.common.collect.toImmutableMap
import xyz.srclab.grpc.spring.boot.server.interceptors.SimpleServerInterceptor
import javax.annotation.Resource

open class DefaultGrpcServersFactory : GrpcServersFactory {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Resource
    private lateinit var grpcServerFactory: GrpcServerFactory

    private val antPathMatcher = AntPathMatcher()

    override fun create(serversConfig: GrpcServersConfig, serverConfigs: Set<GrpcServerConfig>): Map<String, Server> {
        val servers: MutableSetMap<String, GrpcServiceBuilder> =
            mutableMapOf<String, MutableSet<GrpcServiceBuilder>>().toMutableSetMap()

        fun BindableService.matchServers(beanName: String, serviceAnnotation: GrpcService?) {
            if (serviceAnnotation === null || serviceAnnotation.valueOrServerPatterns.isEmpty()) {
                for (serverDefinition in serverConfigs) {
                    servers.add(
                        serverDefinition.name,
                        GrpcServiceBuilder.newBuilder(beanName, this)
                    )
                }
                return
            }
            for (serverPattern in serviceAnnotation.valueOrServerPatterns) {
                for (serverDefinition in serverConfigs) {
                    if (antPathMatcher.match(serverPattern, serverDefinition.name)) {
                        servers.add(
                            serverDefinition.name,
                            GrpcServiceBuilder.newBuilder(beanName, this)
                        )
                    }
                }
            }
        }

        fun GrpcServerInterceptorsBuilder.InterceptorInfo.matchServices() {
            if (this.servicePatterns.isEmpty()) {
                for (serverEntry in servers) {
                    val services = serverEntry.value
                    for (service in services) {
                        service.interceptorsBuilder.addInterceptorInfo(this)
                    }
                }
                return
            }
            for (servicePattern in this.servicePatterns) {
                for (serverEntry in servers) {
                    val services = serverEntry.value
                    for (service in services) {
                        if (antPathMatcher.match(servicePattern, service.beanName)) {
                            service.interceptorsBuilder.addInterceptorInfo(this)
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
            if (serviceAnnotation === null && serversConfig.needGrpcAnnotation) {
                continue
            }
            logger.debug("Load gRPC service: $beanName (${bean.javaClass}).")
            bean.matchServers(beanName, serviceAnnotation)
        }

        //interceptors
        val serverInterceptors = applicationContext.getBeansOfType(ServerInterceptor::class.java)
            .map { key, value ->
                applicationContext.findAnnotationOnBean(key, GrpcServerInterceptor::class.java)
                key to GrpcServerInterceptorsBuilder.newInterceptorInfo(
                    value,
                    applicationContext.findAnnotationOnBean(key, GrpcServerInterceptor::class.java)
                )
            }
            .let {
                if (serversConfig.needGrpcAnnotation) {
                    it.filter { e ->
                        e.value.annotation !== null
                    }
                } else {
                    it
                }
            }

        //simple interceptors
        val simpleInterceptors = applicationContext.getBeansOfType(SimpleServerInterceptor::class.java)
            .map { key, value ->
                applicationContext.findAnnotationOnBean(key, GrpcServerInterceptor::class.java)
                key to GrpcServerInterceptorsBuilder.newInterceptorInfo(
                    value,
                    applicationContext.findAnnotationOnBean(key, GrpcServerInterceptor::class.java)
                )
            }

        for (interceptorEntry in serverInterceptors.plus(simpleInterceptors)) {
            val beanName = interceptorEntry.key
            val info = interceptorEntry.value
            logger.debug("Load gRPC server interceptor: $beanName (${info.javaClass}).")
            info.matchServices()
        }

        //build gRPC server
        val result: MutableMap<String, Server> = mutableMapOf()
        for (serverConfig in serverConfigs) {
            val serviceBuilders = servers[serverConfig.name]
            if (serviceBuilders === null) {
                continue
            }
            result[serverConfig.name] = grpcServerFactory.create(serversConfig, serverConfig, serviceBuilders)
        }
        return result.toImmutableMap()
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(DefaultGrpcServersFactory::class.java)
    }
}