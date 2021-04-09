package xyz.srclab.grpc.spring.boot.server

import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext

/**
 * Factory to create gRPC server ssl context.
 */
interface GrpcServerSslFactory {

    fun create(serverDefinition: GrpcServerDefinition): SslContext?
}