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
    private lateinit var grpcServerBuilderConfigureHelper: GrpcServerBuilderConfigureHelper

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
        serverDefinition: GrpcServerDefinition,
        serviceBuilders: Set<GrpcServiceDefinitionBuilder>
    ): Server {
        return if (serverDefinition.inProcess) createInProcessServer(
            serverDefinition,
            serviceBuilders
        ) else createNettyServer(serverDefinition, serviceBuilders)
    }

    private fun createInProcessServer(
        serverDefinition: GrpcServerDefinition,
        serviceBuilders: Set<GrpcServiceDefinitionBuilder>
    ): Server {
        val builder = InProcessServerBuilder.forName(serverDefinition.name)
        grpcServerBuilderConfigureHelper.configureServices(
            builder,
            serverDefinition,
            serviceBuilders
        )
        logger.info("gRPC in-process-server created: ${serverDefinition.name}")
        return builder.build()
    }

    private fun createNettyServer(
        serverDefinition: GrpcServerDefinition,
        serviceGroupBuilders: Set<GrpcServiceDefinitionBuilder>
    ): Server {
        return if (serverDefinition.useShaded) useShadedNettyServerBuilder(
            serverDefinition,
            serviceGroupBuilders
        ) else useNettyServerBuilder(serverDefinition, serviceGroupBuilders)
    }

    private fun useNettyServerBuilder(
        serverDefinition: GrpcServerDefinition,
        serviceGroupBuilders: Set<GrpcServiceDefinitionBuilder>
    ): Server {
        val builder = NettyServerBuilder.forAddress(InetSocketAddress(serverDefinition.host, serverDefinition.port))
        grpcServerBuilderConfigureHelper.configureServices(builder, serverDefinition, serviceGroupBuilders)
        grpcServerBuilderConfigureHelper.configureExecutor(builder, serverDefinition)
        grpcServerBuilderConfigureHelper.configureSsl(builder, serverDefinition)
        grpcServerBuilderConfigureHelper.configureServerMisc(builder, serverDefinition)

        //configurers
        for (grpcServerConfigurer in grpcServerConfigurers) {
            grpcServerConfigurer.configureNettyServerBuilder(builder, serverDefinition)
        }

        logger.info("gRPC netty-server created: ${serverDefinition.name}")
        return builder.build()
    }

    private fun useShadedNettyServerBuilder(
        serverDefinition: GrpcServerDefinition,
        serviceGroupBuilders: Set<GrpcServiceDefinitionBuilder>
    ): Server {
        val builder =
            ShadedNettyServerBuilder.forAddress(InetSocketAddress(serverDefinition.host, serverDefinition.port))
        grpcServerBuilderConfigureHelper.configureServices(builder, serverDefinition, serviceGroupBuilders)
        grpcServerBuilderConfigureHelper.configureExecutor(builder, serverDefinition)
        grpcServerBuilderConfigureHelper.configureSsl(builder, serverDefinition)
        grpcServerBuilderConfigureHelper.configureServerMisc(builder, serverDefinition)

        //configurers
        for (grpcServerConfigurer in grpcServerConfigurers) {
            grpcServerConfigurer.configureShadedNettyServerBuilder(builder, serverDefinition)
        }

        logger.info("gRPC shaded-netty-server created: ${serverDefinition.name}")
        return builder.build()
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(DefaultGrpcServerFactory::class.java)
    }
}