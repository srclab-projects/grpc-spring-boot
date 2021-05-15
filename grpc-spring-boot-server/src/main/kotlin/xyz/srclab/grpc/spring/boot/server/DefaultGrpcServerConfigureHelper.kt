package xyz.srclab.grpc.spring.boot.server

import io.grpc.ServerBuilder
import io.grpc.netty.GrpcSslContexts
import io.grpc.netty.NettyServerBuilder
import io.netty.handler.ssl.ClientAuth
import org.springframework.context.ApplicationContext
import xyz.srclab.common.lang.loadResource
import xyz.srclab.common.lang.valueOfEnumIgnoreCase
import java.io.File
import java.io.InputStream
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.annotation.Resource
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts as ShadedGrpcSslContexts
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder as ShadedNettyServerBuilder
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth as ShadedClientAuth

open class DefaultGrpcServerConfigureHelper {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    open fun configureServices(
        builder: ServerBuilder<*>, serversConfig: GrpcServersConfig,
        serverConfig: GrpcServerConfig,
        serviceBuilders: Set<GrpcServiceBuilder>,
    ) {
        for (serviceBuilder in serviceBuilders) {
            builder.addService(serviceBuilder.build())
        }
    }

    open fun configureExecutor(
        builder: ServerBuilder<*>,
        serversConfig: GrpcServersConfig,
        serverConfig: GrpcServerConfig
    ) {
        val threadPoolBeanName = serverConfig.threadPoolBeanName
        if (threadPoolBeanName === null) {
            return
        }
        val executor = applicationContext.getBean(threadPoolBeanName)
        if (executor !is Executor) {
            throw IllegalArgumentException("bean $threadPoolBeanName is not a Executor.")
        }
        builder.executor(executor)
    }

    open fun configureSsl(
        builder: NettyServerBuilder,
        serversConfig: GrpcServersConfig,
        serverConfig: GrpcServerConfig
    ) {
        val keyCertChainClassPath = serverConfig.sslCertChainClassPath
        val privateKeyClassPath = serverConfig.sslPrivateKeyClassPath
        val trustCertCollectionClassPath = serverConfig.sslTrustCertCollectionClassPath
        val keyCertChainFile = serverConfig.sslCertChainFile
        val privateKeyFile = serverConfig.sslPrivateKeyFile
        val trustCertCollectionFile = serverConfig.sslTrustCertCollectionFile

        fun openStream(classPath: String?, file: String?): InputStream? {
            return when {
                classPath !== null -> classPath.loadResource().openStream()
                file !== null -> File(file).inputStream()
                else -> null
            }
        }

        val keyCertChainStream = openStream(keyCertChainClassPath, keyCertChainFile)
        if (keyCertChainStream === null) {
            return
        }
        val privateKeyStream = openStream(privateKeyClassPath, privateKeyFile)
        if (privateKeyStream === null) {
            return
        }

        //builder.useTransportSecurity(keyCertChainStream, privateKeyStream)

        val sslBuilder =
            GrpcSslContexts.forServer(keyCertChainStream, privateKeyStream, serverConfig.sslPrivateKeyPassword)

        val clientAuthString = serverConfig.sslClientAuth
        if (clientAuthString !== null) {
            val clientAuth = ClientAuth::class.java.valueOfEnumIgnoreCase<ClientAuth>(clientAuthString)
            if (clientAuth !== ClientAuth.NONE) {
                val trustCertCollectionStream = openStream(trustCertCollectionClassPath, trustCertCollectionFile)
                if (trustCertCollectionStream !== null) {
                    sslBuilder.trustManager(trustCertCollectionStream)
                }
                sslBuilder.clientAuth(clientAuth)
            }
        }

        builder.sslContext(sslBuilder.build())
    }

    open fun configureSsl(
        builder: ShadedNettyServerBuilder,
        serversConfig: GrpcServersConfig,
        serverConfig: GrpcServerConfig
    ) {
        val keyCertChainClassPath = serverConfig.sslCertChainClassPath
        val privateKeyClassPath = serverConfig.sslPrivateKeyClassPath
        val trustCertCollectionClassPath = serverConfig.sslTrustCertCollectionClassPath
        val keyCertChainFile = serverConfig.sslCertChainFile
        val privateKeyFile = serverConfig.sslPrivateKeyFile
        val trustCertCollectionFile = serverConfig.sslTrustCertCollectionFile

        fun openStream(classPath: String?, file: String?): InputStream? {
            return when {
                classPath !== null -> classPath.loadResource().openStream()
                file !== null -> File(file).inputStream()
                else -> null
            }
        }

        val keyCertChainStream = openStream(keyCertChainClassPath, keyCertChainFile)
        if (keyCertChainStream === null) {
            return
        }
        val privateKeyStream = openStream(privateKeyClassPath, privateKeyFile)
        if (privateKeyStream === null) {
            return
        }

        //builder.useTransportSecurity(keyCertChainStream, privateKeyStream)

        val sslBuilder =
            ShadedGrpcSslContexts.forServer(
                keyCertChainStream,
                privateKeyStream,
                serverConfig.sslPrivateKeyPassword
            )

        val clientAuthString = serverConfig.sslClientAuth
        if (clientAuthString !== null) {
            val clientAuth = ShadedClientAuth::class.java.valueOfEnumIgnoreCase<ShadedClientAuth>(clientAuthString)
            if (clientAuth !== ShadedClientAuth.NONE) {
                val trustCertCollectionStream = openStream(trustCertCollectionClassPath, trustCertCollectionFile)
                if (trustCertCollectionStream !== null) {
                    sslBuilder.trustManager(trustCertCollectionStream)
                }
                sslBuilder.clientAuth(clientAuth)
            }
        }

        builder.sslContext(sslBuilder.build())
    }

    open fun configureConnection(
        builder: NettyServerBuilder,
        serversConfig: GrpcServersConfig,
        serverConfig: GrpcServerConfig
    ) {
        val maxConcurrentCallsPerConnection = serverConfig.maxConcurrentCallsPerConnection
        if (maxConcurrentCallsPerConnection !== null) {
            builder.maxConcurrentCallsPerConnection(maxConcurrentCallsPerConnection)
        }
        val initialFlowControlWindow = serverConfig.initialFlowControlWindow
        if (initialFlowControlWindow !== null) {
            builder.initialFlowControlWindow(initialFlowControlWindow)
        }
        val flowControlWindow = serverConfig.flowControlWindow
        if (flowControlWindow !== null) {
            builder.flowControlWindow(flowControlWindow)
        }
        val maxMessageSize = serverConfig.maxMessageSize
        if (maxMessageSize !== null) {
            builder.maxInboundMessageSize(maxMessageSize)
        }
        val maxHeaderListSize = serverConfig.maxHeaderListSize
        if (maxHeaderListSize !== null) {
            builder.maxInboundMetadataSize(maxHeaderListSize)
        }
        val keepAliveTimeInNanos = serverConfig.keepAliveTimeInNanos
        if (keepAliveTimeInNanos !== null) {
            builder.keepAliveTime(keepAliveTimeInNanos, TimeUnit.NANOSECONDS)
        }
        val keepAliveTimeoutInNanos = serverConfig.keepAliveTimeoutInNanos
        if (keepAliveTimeoutInNanos !== null) {
            builder.keepAliveTimeout(keepAliveTimeoutInNanos, TimeUnit.NANOSECONDS)
        }
        val maxConnectionIdleInNanos = serverConfig.maxConnectionIdleInNanos
        if (maxConnectionIdleInNanos !== null) {
            builder.maxConnectionIdle(maxConnectionIdleInNanos, TimeUnit.NANOSECONDS)
        }
        val maxConnectionAgeInNanos = serverConfig.maxConnectionAgeInNanos
        if (maxConnectionAgeInNanos !== null) {
            builder.maxConnectionAge(maxConnectionAgeInNanos, TimeUnit.NANOSECONDS)
        }
        val maxConnectionAgeGraceInNanos = serverConfig.maxConnectionAgeGraceInNanos
        if (maxConnectionAgeGraceInNanos !== null) {
            builder.maxConnectionAgeGrace(maxConnectionAgeGraceInNanos, TimeUnit.NANOSECONDS)
        }
        val permitKeepAliveWithoutCalls = serverConfig.permitKeepAliveWithoutCalls
        if (permitKeepAliveWithoutCalls !== null) {
            builder.permitKeepAliveWithoutCalls(permitKeepAliveWithoutCalls)
        }
        val permitKeepAliveTimeInNanos = serverConfig.permitKeepAliveTimeInNanos
        if (permitKeepAliveTimeInNanos !== null) {
            builder.permitKeepAliveTime(permitKeepAliveTimeInNanos, TimeUnit.NANOSECONDS)
        }
    }

    open fun configureConnection(
        builder: ShadedNettyServerBuilder,
        serversConfig: GrpcServersConfig,
        serverConfig: GrpcServerConfig
    ) {
        val maxConcurrentCallsPerConnection = serverConfig.maxConcurrentCallsPerConnection
        if (maxConcurrentCallsPerConnection !== null) {
            builder.maxConcurrentCallsPerConnection(maxConcurrentCallsPerConnection)
        }
        val initialFlowControlWindow = serverConfig.initialFlowControlWindow
        if (initialFlowControlWindow !== null) {
            builder.initialFlowControlWindow(initialFlowControlWindow)
        }
        val flowControlWindow = serverConfig.flowControlWindow
        if (flowControlWindow !== null) {
            builder.flowControlWindow(flowControlWindow)
        }
        val maxMessageSize = serverConfig.maxMessageSize
        if (maxMessageSize !== null) {
            builder.maxInboundMessageSize(maxMessageSize)
        }
        val maxHeaderListSize = serverConfig.maxHeaderListSize
        if (maxHeaderListSize !== null) {
            builder.maxInboundMetadataSize(maxHeaderListSize)
        }
        val keepAliveTimeInNanos = serverConfig.keepAliveTimeInNanos
        if (keepAliveTimeInNanos !== null) {
            builder.keepAliveTime(keepAliveTimeInNanos, TimeUnit.NANOSECONDS)
        }
        val keepAliveTimeoutInNanos = serverConfig.keepAliveTimeoutInNanos
        if (keepAliveTimeoutInNanos !== null) {
            builder.keepAliveTimeout(keepAliveTimeoutInNanos, TimeUnit.NANOSECONDS)
        }
        val maxConnectionIdleInNanos = serverConfig.maxConnectionIdleInNanos
        if (maxConnectionIdleInNanos !== null) {
            builder.maxConnectionIdle(maxConnectionIdleInNanos, TimeUnit.NANOSECONDS)
        }
        val maxConnectionAgeInNanos = serverConfig.maxConnectionAgeInNanos
        if (maxConnectionAgeInNanos !== null) {
            builder.maxConnectionAge(maxConnectionAgeInNanos, TimeUnit.NANOSECONDS)
        }
        val maxConnectionAgeGraceInNanos = serverConfig.maxConnectionAgeGraceInNanos
        if (maxConnectionAgeGraceInNanos !== null) {
            builder.maxConnectionAgeGrace(maxConnectionAgeGraceInNanos, TimeUnit.NANOSECONDS)
        }
        val permitKeepAliveWithoutCalls = serverConfig.permitKeepAliveWithoutCalls
        if (permitKeepAliveWithoutCalls !== null) {
            builder.permitKeepAliveWithoutCalls(permitKeepAliveWithoutCalls)
        }
        val permitKeepAliveTimeInNanos = serverConfig.permitKeepAliveTimeInNanos
        if (permitKeepAliveTimeInNanos !== null) {
            builder.permitKeepAliveTime(permitKeepAliveTimeInNanos, TimeUnit.NANOSECONDS)
        }
    }
}