package xyz.srclab.grpc.spring.boot.server

import io.grpc.netty.NettyServerBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder as ShadedNettyServerBuilder

/**
 * Configurer for [DefaultGrpcServerFactory].
 */
interface DefaultGrpcServerConfigurer {

    fun configureNettyServerBuilder(builder: NettyServerBuilder, serverDefinition: GrpcServerDefinition)

    fun configureShadedNettyServerBuilder(builder: ShadedNettyServerBuilder, serverDefinition: GrpcServerDefinition)
}