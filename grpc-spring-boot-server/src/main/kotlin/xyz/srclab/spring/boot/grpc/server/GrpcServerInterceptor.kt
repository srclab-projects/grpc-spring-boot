package xyz.srclab.spring.boot.grpc.server

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class GrpcServerInterceptor(

    /**
     * Group name pattern of gRPC service in which interceptor work for, default is empty which means match for all.
     */
    @get:AliasFor("groupPattern")
    val value: Array<String> = [],

    /**
     * Group name pattern of gRPC service in which interceptor work for, default is empty which means match for all.
     */
    @get:AliasFor("value")
    val groupPattern: Array<String> = [],
)