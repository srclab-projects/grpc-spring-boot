package xyz.srclab.grpc.spring.boot.server

import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.util.AntPathMatcher
import xyz.srclab.common.collect.*
import xyz.srclab.common.collect.MutableSetMap.Companion.toMutableSetMap
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
                        GrpcServiceBuilder.newGrpcServiceDefinitionBuilder(beanName, this, null)
                    )
                }
                return
            }
            for (serverPattern in serviceAnnotation.valueOrServerPatterns) {
                for (serverDefinition in serverConfigs) {
                    if (antPathMatcher.match(serverPattern, serverDefinition.name)) {
                        servers.add(
                            serverDefinition.name,
                            GrpcServiceBuilder.newGrpcServiceDefinitionBuilder(beanName, this, null)
                        )
                    }
                }
            }
        }

        fun ServerInterceptorInfo.matchServices() {
            val interceptorAnnotation: GrpcServerInterceptor? = this.annotation
            if (interceptorAnnotation === null || interceptorAnnotation.valueOrServicePatterns.isEmpty()) {
                for (serverEntry in servers) {
                    val services = serverEntry.value
                    for (service in services) {
                        service.interceptors.add(this.interceptor)
                    }
                }
                return
            }
            for (valueOrServicePattern in interceptorAnnotation.valueOrServicePatterns) {
                for (serverEntry in servers) {
                    val services = serverEntry.value
                    for (service in services) {
                        if (antPathMatcher.match(valueOrServicePattern, service.beanName)) {
                            service.interceptors.add(this.interceptor)
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

        //add interceptors
        val serverInterceptors = applicationContext.getBeansOfType(ServerInterceptor::class.java)
            .map { key, value ->
                key to ServerInterceptorInfo(
                    value,
                    applicationContext.findAnnotationOnBean(key, GrpcServerInterceptor::class.java)
                )
            }
            .sorted { e1, e2 ->
                fun Map.Entry<String, ServerInterceptorInfo>.order(): Int {
                    val info = this.value
                    return if (info.annotation === null) 0 else info.annotation.order
                }
                //Note: gRPC interceptors follow the FILO, means first added interceptor will be called last:
                //Add order   : interceptor1, interceptor2, interceptor3
                //Called order: interceptor3, interceptor2, interceptor1
                e2.order() - e1.order()
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
        for (interceptorEntry in serverInterceptors) {
            val beanName = interceptorEntry.key
            val info = interceptorEntry.value
            logger.debug("Load gRPC server interceptor: $beanName (${info.javaClass}).")
            info.matchServices()
        }

        //build gRPC server
        val result: MutableMap<String, Server> = mutableMapOf()
        for (serverDefinition in serverConfigs) {
            val serviceBuilders = servers[serverDefinition.name]
            if (serviceBuilders === null) {
                continue
            }
            result[serverDefinition.name] = grpcServerFactory.create(serversConfig, serverDefinition, serviceBuilders)
        }
        return result.toImmutableMap()
    }

    private data class ServerInterceptorInfo(
        val interceptor: ServerInterceptor,
        val annotation: GrpcServerInterceptor?,
    )

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(DefaultGrpcServersFactory::class.java)
    }
}