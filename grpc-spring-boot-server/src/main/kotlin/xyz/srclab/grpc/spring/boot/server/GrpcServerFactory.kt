package xyz.srclab.grpc.spring.boot.server

import io.grpc.Server

/**
 * Factory to create gRPC server.
 */
interface GrpcServerFactory {

    fun create(serverDefinition: GrpcServerDefinition, serviceBuilders: Set<GrpcServiceDefinitionBuilder>): Server
}