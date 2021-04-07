package xyz.srclab.spring.boot.grpc.server

import io.grpc.BindableService
import io.grpc.ServerBuilder
import io.grpc.ServerInterceptor
import io.grpc.ServerInterceptors
import org.springframework.context.ApplicationContext
import org.springframework.util.AntPathMatcher
import xyz.srclab.common.collect.MutableSetMap
import xyz.srclab.common.collect.toMutableSetMap
import java.util.concurrent.Executor
import javax.annotation.Resource

open class GrpcServerBuilderHelperBean {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    private val antPathMatcher = AntPathMatcher()

    open fun addServices(
        builder: ServerBuilder<*>,
        groupPatterns: List<String>,
        serverName: String,
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
                throw IllegalStateException("No gRPC service found in group $groupPattern for server $serverName")
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

        if (groupPatterns.isEmpty()) {
            addServiceGroupPattern(DEFAULT_GROUP_NAME)
        } else {
            for (groupPattern in groupPatterns) {
                addServiceGroupPattern(groupPattern)
            }
        }
    }

    open fun addExecutor(
        builder: ServerBuilder<*>,
        threadPoolBeanName: String?,
        defaultThreadPoolBeanName: String?,
    ) {
        fun getExecutor(): Executor? {
            val beanName = threadPoolBeanName ?: defaultThreadPoolBeanName
            if (beanName === null) {
                return null
            }
            val executor = applicationContext.getBean(beanName)
            if (executor !is Executor) {
                throw IllegalArgumentException("bean $beanName is not a Executor.")
            }
            return executor
        }

        val executor = getExecutor()
        if (executor !== null) {
            builder.executor(executor)
        }
    }
}