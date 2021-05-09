package xyz.srclab.grpc.spring.boot.server

import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.netty.NettyServerBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder as ShadedNettyServerBuilder

/**
 * Configurer for [DefaultGrpcServerFactory].
 */
interface DefaultGrpcServerConfigurer {

    fun configureInProcessBuilder(
        builder: InProcessServerBuilder,
        serversConfig: GrpcServersConfig,
        serverConfig: GrpcServerConfig
    )

    fun configureNettyBuilder(
        builder: NettyServerBuilder,
        serversConfig: GrpcServersConfig,
        serverConfig: GrpcServerConfig
    )

    fun configureShadedNettyBuilder(
        builder: ShadedNettyServerBuilder,
        serversConfig: GrpcServersConfig,
        serverConfig: GrpcServerConfig
    )
}