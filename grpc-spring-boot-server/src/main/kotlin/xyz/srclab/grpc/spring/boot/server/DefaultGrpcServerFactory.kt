package xyz.srclab.grpc.spring.boot.server

import io.grpc.Server
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.netty.NettyServerBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import java.net.InetSocketAddress
import javax.annotation.PostConstruct
import javax.annotation.Resource
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder as ShadedNettyServerBuilder

open class DefaultGrpcServerFactory : GrpcServerFactory {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Resource
    private lateinit var defaultGrpcServerConfigureHelper: DefaultGrpcServerConfigureHelper

    private lateinit var grpcServerConfigurers: List<DefaultGrpcServerConfigurer>

    @PostConstruct
    private fun init() {
        grpcServerConfigurers = try {
            applicationContext.getBeansOfType(DefaultGrpcServerConfigurer::class.java).values.toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun create(
        serversConfig: GrpcServersConfig,
        serverConfig: GrpcServerConfig,
        serviceBuilders: Set<GrpcServiceBuilder>
    ): Server {
        return if (serverConfig.inProcess)
            createInProcessServer(serversConfig, serverConfig, serviceBuilders)
        else
            createNettyServer(serversConfig, serverConfig, serviceBuilders)
    }

    private fun createInProcessServer(
        serversConfig: GrpcServersConfig,
        serverConfig: GrpcServerConfig,
        serviceBuilders: Set<GrpcServiceBuilder>
    ): Server {
        val builder = InProcessServerBuilder.forName(serverConfig.name)
        defaultGrpcServerConfigureHelper.configureServices(builder, serversConfig, serverConfig, serviceBuilders)
        defaultGrpcServerConfigureHelper.configureExecutor(builder, serversConfig, serverConfig)

        //configurers
        for (grpcServerConfigurer in grpcServerConfigurers) {
            grpcServerConfigurer.configureInProcessBuilder(builder, serversConfig, serverConfig)
        }

        logger.info("gRPC in-process-server created: ${serverConfig.name}")
        return builder.build()
    }

    private fun createNettyServer(
        serversConfig: GrpcServersConfig,
        serverConfig: GrpcServerConfig,
        serviceBuilders: Set<GrpcServiceBuilder>
    ): Server {
        return if (serverConfig.useShaded)
            useShadedNettyServerBuilder(serversConfig, serverConfig, serviceBuilders)
        else
            useNettyServerBuilder(serversConfig, serverConfig, serviceBuilders)
    }

    private fun useNettyServerBuilder(
        serversConfig: GrpcServersConfig,
        serverConfig: GrpcServerConfig,
        serviceBuilders: Set<GrpcServiceBuilder>
    ): Server {
        val builder = NettyServerBuilder.forAddress(InetSocketAddress(serverConfig.host, serverConfig.port))
        defaultGrpcServerConfigureHelper.configureServices(builder, serversConfig, serverConfig, serviceBuilders)
        defaultGrpcServerConfigureHelper.configureExecutor(builder, serversConfig, serverConfig)
        defaultGrpcServerConfigureHelper.configureSsl(builder, serversConfig, serverConfig)
        defaultGrpcServerConfigureHelper.configureConnection(builder, serversConfig, serverConfig)

        //configurers
        for (grpcServerConfigurer in grpcServerConfigurers) {
            grpcServerConfigurer.configureNettyBuilder(builder, serversConfig, serverConfig)
        }

        logger.info("gRPC netty-server created: ${serverConfig.name}")
        return builder.build()
    }

    private fun useShadedNettyServerBuilder(
        serversConfig: GrpcServersConfig,
        serverConfig: GrpcServerConfig,
        serviceBuilders: Set<GrpcServiceBuilder>
    ): Server {
        val builder =
            ShadedNettyServerBuilder.forAddress(InetSocketAddress(serverConfig.host, serverConfig.port))
        defaultGrpcServerConfigureHelper.configureServices(builder, serversConfig, serverConfig, serviceBuilders)
        defaultGrpcServerConfigureHelper.configureExecutor(builder, serversConfig, serverConfig)
        defaultGrpcServerConfigureHelper.configureSsl(builder, serversConfig, serverConfig)
        defaultGrpcServerConfigureHelper.configureConnection(builder, serversConfig, serverConfig)

        //configurers
        for (grpcServerConfigurer in grpcServerConfigurers) {
            grpcServerConfigurer.configureShadedNettyBuilder(builder, serversConfig, serverConfig)
        }

        logger.info("gRPC shaded-netty-server created: ${serverConfig.name}")
        return builder.build()
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(DefaultGrpcServerFactory::class.java)
    }
}