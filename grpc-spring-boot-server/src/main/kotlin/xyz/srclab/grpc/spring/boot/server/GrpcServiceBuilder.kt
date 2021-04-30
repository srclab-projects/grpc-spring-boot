package xyz.srclab.grpc.spring.boot.server

import io.grpc.BindableService
import io.grpc.ServerInterceptor
import io.grpc.ServerInterceptors
import io.grpc.ServerServiceDefinition
import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import java.util.*

interface GrpcServiceBuilder {

    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("beanName")
    @set:JvmName("beanName")
    var beanName: String

    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("service")
    @set:JvmName("service")
    var service: BindableService

    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("interceptors")
    @set:JvmName("interceptors")
    var interceptors: MutableList<ServerInterceptor>

    @JvmDefault
    fun build(): ServerServiceDefinition {
        return if (interceptors.isNullOrEmpty()) {
            service.bindService()
        } else {
            ServerInterceptors.intercept(service.bindService(), interceptors)
        }
    }

    companion object {

        @JvmStatic
        fun newGrpcServiceDefinitionBuilder(
            beanName: String,
            service: BindableService,
            interceptors: Iterable<ServerInterceptor>?
        ): GrpcServiceBuilder {
            return object : GrpcServiceBuilder {
                override var beanName: String = beanName
                override var service: BindableService = service
                override var interceptors: MutableList<ServerInterceptor> =
                    interceptors?.toMutableList() ?: LinkedList()
            }
        }
    }
}