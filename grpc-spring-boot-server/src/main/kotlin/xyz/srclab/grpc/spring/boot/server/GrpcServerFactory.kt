package xyz.srclab.grpc.spring.boot.server

import io.grpc.Server

/**
 * Factory to create gRPC server.
 */
interface GrpcServerFactory {

    fun create(
        serversConfig: GrpcServersConfig,
        serverConfig: GrpcServerConfig,
        serviceBuilders: Set<GrpcServiceBuilder>
    ): Server
}