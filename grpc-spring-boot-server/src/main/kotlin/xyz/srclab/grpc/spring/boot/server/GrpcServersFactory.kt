package xyz.srclab.grpc.spring.boot.server

import io.grpc.Server

/**
 * Factory to create gRPC servers.
 */
interface GrpcServersFactory {

    fun create(serverDefinitions: Set<GrpcServerDefinition>): Map<String, Server>
}