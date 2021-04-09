package xyz.srclab.grpc.spring.boot.client

import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder

/**
 * Factory to create gRPC server ssl context.
 */
interface GrpcShadedNettyChannelConfigurer {

    fun configureChannelBuilder(builder: NettyChannelBuilder, clientDefinition: GrpcClientDefinition)
}