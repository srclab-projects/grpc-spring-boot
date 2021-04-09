package xyz.srclab.grpc.spring.boot.client

import org.springframework.core.annotation.AliasFor
import java.lang.annotation.Inherited

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class GrpcClient(

    /**
     * Name of gRPC client, match for first configured client if empty.
     */
    @get:AliasFor("clientName")
    @get:JvmName("value")
    val value: String = "",

    /**
     * Name of gRPC client, match for first configured client if empty.
     */
    @get:AliasFor("value")
    @get:JvmName("clientName")
    val clientName: String = "",
)
