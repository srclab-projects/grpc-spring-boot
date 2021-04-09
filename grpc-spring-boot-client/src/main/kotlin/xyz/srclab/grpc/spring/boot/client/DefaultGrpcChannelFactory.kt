package xyz.srclab.grpc.spring.boot.client

import io.grpc.Channel
import io.grpc.ClientInterceptor
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import javax.annotation.PostConstruct
import javax.annotation.Resource

open class DefaultGrpcChannelFactory : GrpcChannelFactory {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Resource
    private lateinit var grpcChannelBuilderConfigureHelper: GrpcChannelBuilderConfigureHelper

    private lateinit var grpcShadedNettyChannelConfigurers: List<GrpcShadedNettyChannelConfigurer>

    @PostConstruct
    private fun init() {
        val shadedConfigurer = try {
            applicationContext.getBeansOfType(GrpcShadedNettyChannelConfigurer::class.java)
        } catch (e: Exception) {
            null
        }
        grpcShadedNettyChannelConfigurers = if (shadedConfigurer !== null) {
            shadedConfigurer.values.toList()
        } else {
            emptyList()
        }
    }

    override fun create(clientDefinition: GrpcClientDefinition, interceptors: List<ClientInterceptor>): Channel {
        return if (clientDefinition.inProcess) createInProcessChannel(
            clientDefinition,
            interceptors
        ) else createNettyChannel(clientDefinition, interceptors)
    }

    private fun createInProcessChannel(
        clientDefinition: GrpcClientDefinition,
        interceptors: List<ClientInterceptor>
    ): Channel {
        val builder = InProcessChannelBuilder.forName(clientDefinition.name)
        grpcChannelBuilderConfigureHelper.configureInterceptors(builder, interceptors)
        logger.info("gRPC in-process-channel ${clientDefinition.name} created.")
        return builder.build()
    }

    private fun createNettyChannel(
        clientDefinition: GrpcClientDefinition,
        interceptors: List<ClientInterceptor>
    ): Channel {
        val builder = NettyChannelBuilder.forTarget(clientDefinition.target)
        grpcChannelBuilderConfigureHelper.configureInterceptors(builder, interceptors)
        grpcChannelBuilderConfigureHelper.configureExecutor(builder, clientDefinition)
        grpcChannelBuilderConfigureHelper.configureSsl(builder, clientDefinition)
        grpcChannelBuilderConfigureHelper.configureShadedNettyChannelMisc(builder, clientDefinition)

        //custom configure
        for (grpcShadedNettyChannelConfigurer in grpcShadedNettyChannelConfigurers) {
            grpcShadedNettyChannelConfigurer.configureChannelBuilder(builder, clientDefinition)
        }

        logger.info("gRPC shaded-netty-channel ${clientDefinition.name} created.")
        return builder.build()
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(DefaultGrpcChannelFactory::class.java)
    }
}