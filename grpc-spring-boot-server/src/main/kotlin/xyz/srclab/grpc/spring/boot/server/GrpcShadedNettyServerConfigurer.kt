package xyz.srclab.grpc.spring.boot.server

import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder

/**
 * Factory to create gRPC server ssl context.
 */
interface GrpcShadedNettyServerConfigurer {

    fun configureServerBuilder(builder: NettyServerBuilder, serverDefinition: GrpcServerDefinition)
}