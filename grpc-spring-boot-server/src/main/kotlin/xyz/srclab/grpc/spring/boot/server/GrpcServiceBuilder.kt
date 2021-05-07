package xyz.srclab.grpc.spring.boot.server

import io.grpc.BindableService
import io.grpc.ServerInterceptors
import io.grpc.ServerServiceDefinition
import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME

interface GrpcServiceBuilder {

    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("beanName")
    val beanName: String

    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("service")
    val service: BindableService

    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("interceptorsBuilder")
    val interceptorsBuilder: GrpcServerInterceptorsBuilder

    @JvmDefault
    fun build(): ServerServiceDefinition {
        val interceptors = interceptorsBuilder.build()
        return if (interceptors.isEmpty()) {
            service.bindService()
        } else {
            ServerInterceptors.intercept(service.bindService(), interceptors)
        }
    }

    companion object {

        @JvmStatic
        fun newBuilder(
            beanName: String,
            service: BindableService
        ): GrpcServiceBuilder {
            return object : GrpcServiceBuilder {
                override val beanName: String = beanName
                override val service: BindableService = service
                override val interceptorsBuilder: GrpcServerInterceptorsBuilder =
                    GrpcServerInterceptorsBuilder.newBuilder()
            }
        }
    }
}