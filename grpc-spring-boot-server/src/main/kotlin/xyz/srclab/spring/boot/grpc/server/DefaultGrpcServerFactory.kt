package xyz.srclab.spring.boot.grpc.server

import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerInterceptor
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.netty.NettyServerBuilder
import org.springframework.context.ApplicationContext
import xyz.srclab.common.collect.MutableSetMap
import java.net.InetSocketAddress
import javax.annotation.Resource

open class DefaultGrpcServerFactory : GrpcServerFactory {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Resource
    private lateinit var grpcServerBuilderHelperBean: GrpcServerBuilderHelperBean

    override fun create(
        serverProperties: GrpcServerProperties,
        serversProperties: GrpcServersProperties,
        serviceGroups: MutableSetMap<String, BindableService>,
        interceptorGroups: MutableSetMap<String, ServerInterceptor>
    ): Server {
        return if (serverProperties.inProcess) createInProcessServer(
            serverProperties,
            serversProperties,
            serviceGroups,
            interceptorGroups
        ) else createNettyServer(serverProperties, serversProperties, serviceGroups, interceptorGroups)
    }

    private fun createInProcessServer(
        serverProperties: GrpcServerProperties,
        serversProperties: GrpcServersProperties,
        serviceGroups: MutableSetMap<String, BindableService>,
        interceptorGroups: MutableSetMap<String, ServerInterceptor>
    ): Server {
        val builder = InProcessServerBuilder.forName(serverProperties.name)
        grpcServerBuilderHelperBean.addService(
            builder,
            serverProperties.groupPatterns,
            serverProperties.name,
            serviceGroups,
            interceptorGroups
        )
        return builder.build()
    }

    private fun createNettyServer(
        serverProperties: GrpcServerProperties,
        serversProperties: GrpcServersProperties,
        serviceGroups: MutableSetMap<String, BindableService>,
        interceptorGroups: MutableSetMap<String, ServerInterceptor>
    ): Server {
        val builder = NettyServerBuilder.forAddress(InetSocketAddress(serverProperties.ip, serverProperties.port))
        grpcServerBuilderHelperBean.addService(
            builder,
            serverProperties.groupPatterns,
            serverProperties.name,
            serviceGroups,
            interceptorGroups
        )
        grpcServerBuilderHelperBean.addExecutor(
            builder,
            serverProperties.threadPoolBeanName,
            serversProperties.defaultThreadPoolBeanName
        )
        return builder.build()
    }
}