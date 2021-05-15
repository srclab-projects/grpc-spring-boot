package xyz.srclab.grpc.spring.boot.client

import io.grpc.ClientInterceptor
import io.grpc.ManagedChannelBuilder
import io.grpc.netty.GrpcSslContexts
import io.grpc.netty.NettyChannelBuilder
import io.netty.handler.ssl.ClientAuth
import org.springframework.context.ApplicationContext
import xyz.srclab.common.lang.loadResource
import xyz.srclab.common.lang.valueOfEnumIgnoreCaseOrNull
import java.io.File
import java.io.InputStream
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.annotation.Resource
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts as ShadedGrpcSslContexts
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder as ShadedNettyChannelBuilder
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth as ShadedClientAuth

open class DefaultGrpcChannelConfigureHelper {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    open fun configureInterceptors(builder: ManagedChannelBuilder<*>, interceptors: List<ClientInterceptor>) {
        builder.intercept(interceptors)
    }

    open fun configureExecutor(builder: ManagedChannelBuilder<*>, clientConfig: GrpcClientConfig) {
        val threadPoolBeanName = clientConfig.threadPoolBeanName
        if (threadPoolBeanName === null) {
            return
        }
        val executor = applicationContext.getBean(threadPoolBeanName)
        if (executor !is Executor) {
            throw IllegalArgumentException("bean $threadPoolBeanName is not a Executor.")
        }
        builder.executor(executor)
    }

    open fun configureSsl(builder: NettyChannelBuilder, clientConfig: GrpcClientConfig) {
        val keyCertChainClassPath = clientConfig.sslCertChainClassPath
        val privateKeyClassPath = clientConfig.sslPrivateKeyClassPath
        val trustCertCollectionClassPath = clientConfig.sslTrustCertCollectionClassPath
        val keyCertChainFile = clientConfig.sslCertChainFile
        val privateKeyFile = clientConfig.sslPrivateKeyFile
        val trustCertCollectionFile = clientConfig.sslTrustCertCollectionFile

        fun openStream(classPath: String?, file: String?): InputStream? {
            return when {
                classPath !== null -> classPath.loadResource().openStream()
                file !== null -> File(file).inputStream()
                else -> null
            }
        }

        val keyCertChainStream = openStream(keyCertChainClassPath, keyCertChainFile)
        if (keyCertChainStream === null) {
            builder.usePlaintext()
            return
        }
        val privateKeyStream = openStream(privateKeyClassPath, privateKeyFile)
        if (privateKeyStream === null) {
            builder.usePlaintext()
            return
        }

        val sslBuilder = GrpcSslContexts.forClient()
            .keyManager(
                keyCertChainStream,
                privateKeyStream,
                clientConfig.sslPrivateKeyPassword
            )
        val trustCertCollectionStream = openStream(trustCertCollectionClassPath, trustCertCollectionFile)
        if (trustCertCollectionStream !== null) {
            sslBuilder.trustManager(trustCertCollectionStream)
        }

        val clientAuthString = clientConfig.sslClientAuth
        if (clientAuthString !== null) {
            val clientAuth = ClientAuth::class.java.valueOfEnumIgnoreCaseOrNull<ClientAuth>(clientAuthString)
            if (clientAuth !== ClientAuth.NONE)
                sslBuilder.clientAuth(clientAuth)
        }

        builder.useTransportSecurity()
        builder.sslContext(sslBuilder.build())
    }

    open fun configureSsl(builder: ShadedNettyChannelBuilder, clientConfig: GrpcClientConfig) {
        val keyCertChainClassPath = clientConfig.sslCertChainClassPath
        val privateKeyClassPath = clientConfig.sslPrivateKeyClassPath
        val trustCertCollectionClassPath = clientConfig.sslTrustCertCollectionClassPath
        val keyCertChainFile = clientConfig.sslCertChainFile
        val privateKeyFile = clientConfig.sslPrivateKeyFile
        val trustCertCollectionFile = clientConfig.sslTrustCertCollectionFile

        fun openStream(classPath: String?, file: String?): InputStream? {
            return when {
                classPath !== null -> classPath.loadResource().openStream()
                file !== null -> File(file).inputStream()
                else -> null
            }
        }

        val keyCertChainStream = openStream(keyCertChainClassPath, keyCertChainFile)
        if (keyCertChainStream === null) {
            builder.usePlaintext()
            return
        }
        val privateKeyStream = openStream(privateKeyClassPath, privateKeyFile)
        if (privateKeyStream === null) {
            builder.usePlaintext()
            return
        }

        val sslBuilder = ShadedGrpcSslContexts.forClient()
            .keyManager(
                keyCertChainStream,
                privateKeyStream,
                clientConfig.sslPrivateKeyPassword
            )
        val trustCertCollectionStream = openStream(trustCertCollectionClassPath, trustCertCollectionFile)
        if (trustCertCollectionStream !== null) {
            sslBuilder.trustManager(trustCertCollectionStream)
        }

        val clientAuthString = clientConfig.sslClientAuth
        if (clientAuthString !== null) {
            val clientAuth =
                ShadedClientAuth::class.java.valueOfEnumIgnoreCaseOrNull<ShadedClientAuth>(clientAuthString)
            if (clientAuth !== ShadedClientAuth.NONE)
                sslBuilder.clientAuth(clientAuth)
        }

        builder.useTransportSecurity()
        builder.sslContext(sslBuilder.build())
    }

    open fun configureConnection(builder: NettyChannelBuilder, clientConfig: GrpcClientConfig) {
        val initialFlowControlWindow = clientConfig.initialFlowControlWindow
        if (initialFlowControlWindow !== null) {
            builder.initialFlowControlWindow(initialFlowControlWindow)
        }
        val flowControlWindow = clientConfig.flowControlWindow
        if (flowControlWindow !== null) {
            builder.flowControlWindow(flowControlWindow)
        }
        val maxMessageSize = clientConfig.maxMessageSize
        if (maxMessageSize !== null) {
            builder.maxInboundMessageSize(maxMessageSize)
        }
        val maxHeaderListSize = clientConfig.maxHeaderListSize
        if (maxHeaderListSize !== null) {
            builder.maxInboundMetadataSize(maxHeaderListSize)
        }
        val keepAliveTimeInNanos = clientConfig.keepAliveTimeInNanos
        if (keepAliveTimeInNanos !== null) {
            builder.keepAliveTime(keepAliveTimeInNanos, TimeUnit.NANOSECONDS)
        }
        val keepAliveTimeoutInNanos = clientConfig.keepAliveTimeoutInNanos
        if (keepAliveTimeoutInNanos !== null) {
            builder.keepAliveTimeout(keepAliveTimeoutInNanos, TimeUnit.NANOSECONDS)
        }
        val keepAliveWithoutCalls = clientConfig.keepAliveWithoutCalls
        if (keepAliveWithoutCalls !== null) {
            builder.keepAliveWithoutCalls(keepAliveWithoutCalls)
        }
        val loadBalancingPolicy = clientConfig.loadBalancingPolicy
        if (loadBalancingPolicy !== null) {
            builder.defaultLoadBalancingPolicy(loadBalancingPolicy)
        }
    }

    open fun configureConnection(builder: ShadedNettyChannelBuilder, clientConfig: GrpcClientConfig) {
        val initialFlowControlWindow = clientConfig.initialFlowControlWindow
        if (initialFlowControlWindow !== null) {
            builder.initialFlowControlWindow(initialFlowControlWindow)
        }
        val flowControlWindow = clientConfig.flowControlWindow
        if (flowControlWindow !== null) {
            builder.flowControlWindow(flowControlWindow)
        }
        val maxMessageSize = clientConfig.maxMessageSize
        if (maxMessageSize !== null) {
            builder.maxInboundMessageSize(maxMessageSize)
        }
        val maxHeaderListSize = clientConfig.maxHeaderListSize
        if (maxHeaderListSize !== null) {
            builder.maxInboundMetadataSize(maxHeaderListSize)
        }
        val keepAliveTimeInNanos = clientConfig.keepAliveTimeInNanos
        if (keepAliveTimeInNanos !== null) {
            builder.keepAliveTime(keepAliveTimeInNanos, TimeUnit.NANOSECONDS)
        }
        val keepAliveTimeoutInNanos = clientConfig.keepAliveTimeoutInNanos
        if (keepAliveTimeoutInNanos !== null) {
            builder.keepAliveTimeout(keepAliveTimeoutInNanos, TimeUnit.NANOSECONDS)
        }
        val keepAliveWithoutCalls = clientConfig.keepAliveWithoutCalls
        if (keepAliveWithoutCalls !== null) {
            builder.keepAliveWithoutCalls(keepAliveWithoutCalls)
        }
        builder.defaultLoadBalancingPolicy(clientConfig.loadBalancingPolicy)
    }
}