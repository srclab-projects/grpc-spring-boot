package xyz.srclab.grpc.spring.boot.server

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class GrpcServerInterceptor(

    /**
     * Bean name patterns of gRPC service in which interceptor work for, default is empty which means match for all.
     */
    @get:AliasFor("servicePatterns")
    @get:JvmName("value")
    val value: Array<String> = [],

    /**
     * Bean name patterns of gRPC service in which interceptor work for, default is empty which means match for all.
     */
    @get:AliasFor("value")
    @get:JvmName("servicePatterns")
    val servicePatterns: Array<String> = [],
)