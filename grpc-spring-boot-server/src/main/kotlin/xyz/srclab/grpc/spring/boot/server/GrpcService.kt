package xyz.srclab.grpc.spring.boot.server

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class GrpcService(

    /**
     * Server name patterns of gRPC, default is empty means match for all.
     */
    @get:AliasFor("serverPatterns")
    @get:JvmName("value")
    val value: Array<String> = [],

    /**
     * Server name patterns of gRPC, default is empty means match for all.
     */
    @get:JvmName("serverPatterns")
    val serverPatterns: Array<String> = [],
)
