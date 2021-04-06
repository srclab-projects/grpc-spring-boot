package xyz.srclab.spring.boot.grpc.server

import io.grpc.Server

/**
 * Factory to create gRPC server.
 */
interface GrpcServersFactory {

    fun create(serversProperties: GrpcServersProperties): Map<String, Server>
}