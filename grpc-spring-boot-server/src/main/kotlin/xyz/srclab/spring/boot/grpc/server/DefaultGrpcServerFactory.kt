package xyz.srclab.spring.boot.grpc.server

import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerInterceptor
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.netty.NettyServerBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import xyz.srclab.common.collect.MutableSetMap
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

open class DefaultGrpcServerFactory : GrpcServerFactory {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Resource
    private lateinit var grpcServerBuilderHelperBean: GrpcServerBuilderHelperBean

    override fun create(
        serverName: String,
        serverProperties: GrpcServerProperties,
        serversProperties: GrpcServersProperties,
        serviceGroups: MutableSetMap<String, BindableService>,
        interceptorGroups: MutableSetMap<String, ServerInterceptor>
    ): Server {
        return if (serverProperties.inProcess) createInProcessServer(
            serverName,
            serverProperties,
            serversProperties,
            serviceGroups,
            interceptorGroups
        ) else createNettyServer(serverName, serverProperties, serversProperties, serviceGroups, interceptorGroups)
    }

    private fun createInProcessServer(
        serverName: String,
        serverProperties: GrpcServerProperties,
        serversProperties: GrpcServersProperties,
        serviceGroups: MutableSetMap<String, BindableService>,
        interceptorGroups: MutableSetMap<String, ServerInterceptor>
    ): Server {
        val builder = InProcessServerBuilder.forName(serverName)
        grpcServerBuilderHelperBean.addServices(
            builder,
            serverProperties.groupPatterns,
            serverName,
            serviceGroups,
            interceptorGroups
        )
        logger.info("gRPC in-process-server $serverName created.")
        return builder.build()
    }

    private fun createNettyServer(
        serverName: String,
        serverProperties: GrpcServerProperties,
        serversProperties: GrpcServersProperties,
        serviceGroups: MutableSetMap<String, BindableService>,
        interceptorGroups: MutableSetMap<String, ServerInterceptor>
    ): Server {
        val builder = NettyServerBuilder.forAddress(InetSocketAddress(serverProperties.ip, serverProperties.port))
        grpcServerBuilderHelperBean.addServices(
            builder,
            serverProperties.groupPatterns,
            serverName,
            serviceGroups,
            interceptorGroups
        )
        grpcServerBuilderHelperBean.addExecutor(
            builder,
            serverProperties.threadPoolBeanName,
            serversProperties.defaultThreadPoolBeanName
        )

        builder.maxConcurrentCallsPerConnection(serverProperties.maxConcurrentCallsPerConnection)
        builder.flowControlWindow(serverProperties.flowControlWindow)
        builder.maxInboundMessageSize(serverProperties.maxMessageSize)
        builder.maxInboundMetadataSize(serverProperties.maxHeaderListSize)
        builder.keepAliveTime(serverProperties.keepAliveTimeInNanos, TimeUnit.NANOSECONDS)
        builder.keepAliveTimeout(serverProperties.keepAliveTimeoutInNanos, TimeUnit.NANOSECONDS)
        builder.maxConnectionIdle(serverProperties.maxConnectionIdleInNanos, TimeUnit.NANOSECONDS)
        builder.maxConnectionAge(serverProperties.maxConnectionAgeInNanos, TimeUnit.NANOSECONDS)
        builder.maxConnectionAgeGrace(serverProperties.maxConnectionAgeGraceInNanos, TimeUnit.NANOSECONDS)
        builder.permitKeepAliveWithoutCalls(serverProperties.permitKeepAliveWithoutCalls)
        builder.permitKeepAliveTime(serverProperties.permitKeepAliveTimeInNanos, TimeUnit.NANOSECONDS)

        //builder.sslContext()

        logger.info("gRPC netty-server $serverName created.")
        return builder.build()
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(DefaultGrpcServerFactory::class.java)
    }
}