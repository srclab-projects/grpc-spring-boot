package xyz.srclab.grpc.spring.boot.client

import io.grpc.Channel
import io.grpc.ClientInterceptor
import io.grpc.NameResolverRegistry
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.netty.NettyChannelBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import javax.annotation.PostConstruct
import javax.annotation.Resource
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder as ShadedNettyChannelBuilder

open class DefaultGrpcChannelFactory : GrpcChannelFactory {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Resource
    private lateinit var defaultGrpcChannelConfigureHelper: DefaultGrpcChannelConfigureHelper

    @Resource
    private lateinit var defaultGrpcTargetResolver: GrpcTargetResolver

    private lateinit var defaultGrpcChannelConfigurers: List<DefaultGrpcChannelConfigurer>

    @PostConstruct
    private fun init() {
        defaultGrpcChannelConfigurers = try {
            applicationContext.getBeansOfType(DefaultGrpcChannelConfigurer::class.java).values.toList()
        } catch (e: Exception) {
            emptyList()
        }

        //add load balance support: lb:authority/host1:port1,host2:port2
        NameResolverRegistry.getDefaultRegistry().register(LbNameResolverProvider(defaultGrpcTargetResolver))
    }

    override fun create(
        clientsConfig: GrpcClientsConfig,
        clientConfig: GrpcClientConfig,
        interceptors: List<ClientInterceptor>
    ): Channel {
        return if (clientConfig.inProcess)
            createInProcessChannel(clientsConfig, clientConfig, interceptors)
        else
            createNettyChannel(clientsConfig, clientConfig, interceptors)
    }

    private fun createInProcessChannel(
        clientsConfig: GrpcClientsConfig,
        clientConfig: GrpcClientConfig,
        interceptors: List<ClientInterceptor>
    ): Channel {
        val builder = InProcessChannelBuilder.forName(clientConfig.name)
        defaultGrpcChannelConfigureHelper.configureInterceptors(builder, interceptors)
        defaultGrpcChannelConfigureHelper.configureExecutor(builder, clientConfig)

        //custom configure
        for (defaultGrpcChannelConfigurer in defaultGrpcChannelConfigurers) {
            defaultGrpcChannelConfigurer.configureInProcessBuilder(builder, clientsConfig, clientConfig)
        }

        logger.info("gRPC in-process-channel created: ${clientConfig.name}")
        return builder.build()
    }

    private fun createNettyChannel(
        clientsConfig: GrpcClientsConfig,
        clientConfig: GrpcClientConfig,
        interceptors: List<ClientInterceptor>
    ): Channel {
        return if (clientConfig.useShaded)
            useShadedNettyChannelBuilder(clientsConfig, clientConfig, interceptors)
        else
            useNettyChannelBuilder(clientsConfig, clientConfig, interceptors)
    }

    private fun useNettyChannelBuilder(
        clientsConfig: GrpcClientsConfig,
        clientConfig: GrpcClientConfig,
        interceptors: List<ClientInterceptor>
    ): Channel {
        val builder = NettyChannelBuilder.forTarget(clientConfig.target)
        defaultGrpcChannelConfigureHelper.configureInterceptors(builder, interceptors)
        defaultGrpcChannelConfigureHelper.configureExecutor(builder, clientConfig)
        defaultGrpcChannelConfigureHelper.configureSsl(builder, clientConfig)
        defaultGrpcChannelConfigureHelper.configureConnection(builder, clientConfig)

        //custom configure
        for (defaultGrpcChannelConfigurer in defaultGrpcChannelConfigurers) {
            defaultGrpcChannelConfigurer.configureNettyBuilder(builder, clientsConfig, clientConfig)
        }

        logger.info("gRPC netty-channel created: ${clientConfig.name}")
        return builder.build()
    }

    private fun useShadedNettyChannelBuilder(
        clientsConfig: GrpcClientsConfig,
        clientConfig: GrpcClientConfig,
        interceptors: List<ClientInterceptor>
    ): Channel {
        val builder = ShadedNettyChannelBuilder.forTarget(clientConfig.target)
        defaultGrpcChannelConfigureHelper.configureInterceptors(builder, interceptors)
        defaultGrpcChannelConfigureHelper.configureExecutor(builder, clientConfig)
        defaultGrpcChannelConfigureHelper.configureSsl(builder, clientConfig)
        defaultGrpcChannelConfigureHelper.configureConnection(builder, clientConfig)

        //custom configure
        for (defaultGrpcChannelConfigurer in defaultGrpcChannelConfigurers) {
            defaultGrpcChannelConfigurer.configureShadedNettyBuilder(builder, clientsConfig, clientConfig)
        }

        logger.info("gRPC shaded-netty-channel created: ${clientConfig.name}")
        return builder.build()
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(DefaultGrpcChannelFactory::class.java)
    }
}