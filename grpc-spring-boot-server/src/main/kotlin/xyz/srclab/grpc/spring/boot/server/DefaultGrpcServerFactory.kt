package xyz.srclab.grpc.spring.boot.server

import io.grpc.Server
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.Resource

open class DefaultGrpcServerFactory : GrpcServerFactory {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Resource
    private lateinit var grpcServerBuilderConfigureHelper: GrpcServerBuilderConfigureHelper

    private lateinit var grpcShadedNettyServerConfigurers: List<GrpcShadedNettyServerConfigurer>

    @PostConstruct
    private fun init() {
        val shadedConfigurer = try {
            applicationContext.getBeansOfType(GrpcShadedNettyServerConfigurer::class.java)
        } catch (e: Exception) {
            null
        }
        grpcShadedNettyServerConfigurers = if (shadedConfigurer !== null) {
            shadedConfigurer.values.toList()
        } else {
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
        logger.info("gRPC in-process-server ${serverDefinition.name} created.")
        return builder.build()
    }

    private fun createNettyServer(
        serverDefinition: GrpcServerDefinition,
        serviceGroupBuilders: Set<GrpcServiceDefinitionBuilder>
    ): Server {
        val builder = NettyServerBuilder.forAddress(InetSocketAddress(serverDefinition.host, serverDefinition.port))
        grpcServerBuilderConfigureHelper.configureServices(
            builder,
            serverDefinition,
            serviceGroupBuilders
        )
        grpcServerBuilderConfigureHelper.configureExecutor(
            builder,
            serverDefinition
        )

        builder.maxConcurrentCallsPerConnection(serverDefinition.maxConcurrentCallsPerConnection)
        builder.flowControlWindow(serverDefinition.flowControlWindow)
        builder.maxInboundMessageSize(serverDefinition.maxMessageSize)
        builder.maxInboundMetadataSize(serverDefinition.maxHeaderListSize)
        builder.keepAliveTime(serverDefinition.keepAliveTimeInNanos, TimeUnit.NANOSECONDS)
        builder.keepAliveTimeout(serverDefinition.keepAliveTimeoutInNanos, TimeUnit.NANOSECONDS)
        builder.maxConnectionIdle(serverDefinition.maxConnectionIdleInNanos, TimeUnit.NANOSECONDS)
        builder.maxConnectionAge(serverDefinition.maxConnectionAgeInNanos, TimeUnit.NANOSECONDS)
        builder.maxConnectionAgeGrace(serverDefinition.maxConnectionAgeGraceInNanos, TimeUnit.NANOSECONDS)
        builder.permitKeepAliveWithoutCalls(serverDefinition.permitKeepAliveWithoutCalls)
        builder.permitKeepAliveTime(serverDefinition.permitKeepAliveTimeInNanos, TimeUnit.NANOSECONDS)

        //custom configure
        for (grpcShadedNettyServerConfigurer in grpcShadedNettyServerConfigurers) {
            grpcShadedNettyServerConfigurer.configureServerBuilder(builder, serverDefinition)
        }

        logger.info("gRPC shaded-netty-server ${serverDefinition.name} created.")
        return builder.build()
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(DefaultGrpcServerFactory::class.java)
    }
}