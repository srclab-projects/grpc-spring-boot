package xyz.srclab.grpc.spring.boot.client

import io.grpc.Channel
import io.grpc.ClientInterceptor

/**
 * Factory to create gRPC server.
 */
interface GrpcChannelFactory {

    fun create(
        clientsConfig: GrpcClientsConfig,
        clientConfig: GrpcClientConfig,
        interceptors: List<ClientInterceptor>
    ): Channel
}