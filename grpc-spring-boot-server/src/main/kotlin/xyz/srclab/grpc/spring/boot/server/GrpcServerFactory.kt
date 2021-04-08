package xyz.srclab.grpc.spring.boot.server

import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerInterceptor
import xyz.srclab.common.collect.MutableSetMap

/**
 * Factory to create gRPC server.
 */
interface GrpcServerFactory {

    fun create(
        serverDefinition: GrpcServerDefinition,
        serviceGroups: MutableSetMap<String, BindableService>,
        interceptorGroups: MutableSetMap<String, ServerInterceptor>,
    ): Server
}