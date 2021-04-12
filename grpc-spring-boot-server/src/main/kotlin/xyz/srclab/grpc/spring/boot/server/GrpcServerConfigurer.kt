package xyz.srclab.grpc.spring.boot.server

import io.grpc.ServerBuilder

/**
 * Factory to create gRPC server ssl context.
 */
interface GrpcServerConfigurer<T : ServerBuilder<T>> {

    fun configureServerBuilder(builder: ServerBuilder<T>, serverDefinition: GrpcServerDefinition)
}