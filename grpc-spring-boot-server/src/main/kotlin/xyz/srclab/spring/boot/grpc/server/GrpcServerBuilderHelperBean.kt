package xyz.srclab.spring.boot.grpc.server

import io.grpc.BindableService
import io.grpc.ServerBuilder
import io.grpc.ServerInterceptor
import io.grpc.ServerInterceptors
import org.springframework.context.ApplicationContext
import org.springframework.util.AntPathMatcher
import java.util.concurrent.Executor
import javax.annotation.Resource

open class GrpcServerBuilderHelperBean {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    private val antPathMatcher = AntPathMatcher()

    open fun addService(
        builder: ServerBuilder<*>,
        groupPatterns: List<String>,
        serverName: String,
        serviceGroups: Map<String, Set<BindableService>>,
        interceptorGroups: Map<String, Set<ServerInterceptor>>
    ) {
        fun addServiceGroup(group: String) {
            val services = serviceGroups[group]
            if (services.isNullOrEmpty()) {
                throw IllegalStateException("No gRPC service found in group $group for server $serverName")
            }
            val interceptors: MutableSet<ServerInterceptor> = mutableSetOf()
            for (interceptorEntry in interceptorGroups) {
                val groupPattern = interceptorEntry.key
                val interceptor = interceptorEntry.value
                if (antPathMatcher.match(groupPattern, group)) {
                    interceptors.addAll(interceptor)
                }
            }
            if (interceptors.isEmpty()) {
                for (bindableService in services) {
                    builder.addService(bindableService)
                }
            } else {
                for (bindableService in services) {
                    val service = ServerInterceptors.intercept(bindableService, *interceptors.toTypedArray())
                    builder.addService(service)
                }
            }
        }

        if (groupPatterns.isEmpty()) {
            addServiceGroup(DEFAULT_GROUP_NAME)
        } else {
            for (groupPattern in groupPatterns) {
                addServiceGroup(groupPattern)
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