package xyz.srclab.grpc.spring.boot.client

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class GrpcClientInterceptor(

    /**
     * Client name patterns of gRPC client in which interceptor work for, default is empty which means match for all.
     */
    @get:AliasFor("clientPatterns")
    @get:JvmName("value")
    val value: Array<String> = [],

    /**
     * Client name patterns of gRPC client in which interceptor work for, default is empty which means match for all.
     */
    @get:AliasFor("value")
    @get:JvmName("clientPatterns")
    val clientPatterns: Array<String> = [],
)