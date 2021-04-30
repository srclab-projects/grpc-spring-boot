package xyz.srclab.grpc.spring.boot.server

import io.grpc.Server

/**
 * Factory to create gRPC servers.
 */
interface GrpcServersFactory {

    fun create(serversConfig: GrpcServersConfig, serverConfigs: Set<GrpcServerConfig>): Map<String, Server>
}