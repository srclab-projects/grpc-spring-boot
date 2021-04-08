package xyz.srclab.grpc.spring.boot.server

import io.grpc.BindableService
import io.grpc.ServerBuilder
import io.grpc.ServerInterceptor
import io.grpc.ServerInterceptors
import io.grpc.netty.NettyServerBuilder
import org.springframework.context.ApplicationContext
import org.springframework.util.AntPathMatcher
import xyz.srclab.common.collect.MutableSetMap
import xyz.srclab.common.collect.toMutableSetMap
import java.util.concurrent.Executor
import javax.annotation.Resource

open class GrpcServerBuilderConfigureHelper {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Resource
    private lateinit var grpcServerSslFactory: GrpcServerSslFactory

    private val antPathMatcher = AntPathMatcher()

    open fun configureServices(
        builder: ServerBuilder<*>,
        serverDefinition: GrpcServerDefinition,
        serviceGroups: Map<String, Set<BindableService>>,
        interceptorGroups: Map<String, Set<ServerInterceptor>>
    ) {
        fun addServiceGroupPattern(groupPattern: String) {
            val services: MutableSetMap<String, BindableService> =
                mutableMapOf<String, MutableSet<BindableService>>().toMutableSetMap()
            for (serviceEntry in serviceGroups) {
                val group = serviceEntry.key
                val service = serviceEntry.value
                if (antPathMatcher.match(groupPattern, group)) {
                    services.addAll(group, service)
                }
            }
            if (services.isNullOrEmpty()) {
                throw IllegalStateException(
                    "No gRPC service found in group $groupPattern for server ${serverDefinition.name}"
                )
            }
            for (serviceEntry in services) {
                val group = serviceEntry.key
                val groupServices = serviceEntry.value
                val interceptors: MutableSet<ServerInterceptor> = mutableSetOf()
                for (interceptorEntry in interceptorGroups) {
                    val interceptorGroupPattern = interceptorEntry.key
                    val interceptor = interceptorEntry.value
                    if (interceptorGroupPattern.isEmpty() || antPathMatcher.match(interceptorGroupPattern, group)) {
                        interceptors.addAll(interceptor)
                    }
                }
                if (interceptors.isEmpty()) {
                    for (bindableService in groupServices) {
                        builder.addService(bindableService)
                    }
                } else {
                    for (bindableService in groupServices) {
                        val service = ServerInterceptors.intercept(bindableService, *interceptors.toTypedArray())
                        builder.addService(service)
                    }
                }
            }
        }

        if (serverDefinition.groupPatterns.isEmpty()) {
            addServiceGroupPattern(DEFAULT_GROUP_NAME)
        } else {
            for (groupPattern in serverDefinition.groupPatterns) {
                addServiceGroupPattern(groupPattern)
            }
        }
    }

    open fun configureExecutor(
        builder: ServerBuilder<*>,
        serverDefinition: GrpcServerDefinition,
    ) {
        val threadPoolBeanName = serverDefinition.threadPoolBeanName
        if (threadPoolBeanName === null) {
            return
        }
        val executor = applicationContext.getBean(threadPoolBeanName)
        if (executor !is Executor) {
            throw IllegalArgumentException("bean $threadPoolBeanName is not a Executor.")
        }
        builder.executor(executor)
    }

    open fun configureSsl(
        builder: NettyServerBuilder,
        serverDefinition: GrpcServerDefinition,
    ) {
        val sslContext = grpcServerSslFactory.create(serverDefinition)
        if (sslContext === null) {
            return
        }
        builder.sslContext(sslContext)
    }
}