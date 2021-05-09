package xyz.srclab.grpc.spring.boot.client

import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.netty.NettyChannelBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder as ShadedNettyChannelBuilder

/**
 * Factory to create gRPC server ssl context.
 */
interface DefaultGrpcChannelConfigurer {

    fun configureInProcessBuilder(
        builder: InProcessChannelBuilder,
        clientsConfig: GrpcClientsConfig,
        clientConfig: GrpcClientConfig
    )

    fun configureNettyBuilder(
        builder: NettyChannelBuilder,
        clientsConfig: GrpcClientsConfig,
        clientConfig: GrpcClientConfig
    )

    fun configureShadedNettyBuilder(
        builder: ShadedNettyChannelBuilder,
        clientsConfig: GrpcClientsConfig,
        clientConfig: GrpcClientConfig
    )
}