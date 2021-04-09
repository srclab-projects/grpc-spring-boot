package xyz.srclab.grpc.spring.boot.client

import io.grpc.ClientInterceptor
import io.grpc.ManagedChannelBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder
import org.springframework.context.ApplicationContext
import xyz.srclab.common.base.enumValueOfIgnoreCase
import xyz.srclab.common.base.loadResource
import java.io.File
import java.io.InputStream
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

open class GrpcChannelBuilderConfigureHelper {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    open fun configureInterceptors(builder: ManagedChannelBuilder<*>, interceptors: List<ClientInterceptor>) {
        builder.intercept(interceptors)
    }

    open fun configureExecutor(builder: ManagedChannelBuilder<*>, clientDefinition: GrpcClientDefinition) {
        val threadPoolBeanName = clientDefinition.threadPoolBeanName
        if (threadPoolBeanName === null) {
            return
        }
        val executor = applicationContext.getBean(threadPoolBeanName)
        if (executor !is Executor) {
            throw IllegalArgumentException("bean $threadPoolBeanName is not a Executor.")
        }
        builder.executor(executor)
    }

    open fun configureSsl(builder: NettyChannelBuilder, clientDefinition: GrpcClientDefinition) {
        val keyCertChainClassPath = clientDefinition.sslCertChainClassPath
        val privateKeyClassPath = clientDefinition.sslPrivateKeyClassPath
        val trustCertCollectionClassPath = clientDefinition.sslTrustCertCollectionClassPath
        val keyCertChainFile = clientDefinition.sslCertChainFile
        val privateKeyFile = clientDefinition.sslPrivateKeyFile
        val trustCertCollectionFile = clientDefinition.sslTrustCertCollectionFile

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

        val sslBuilder = SslContextBuilder.forClient()
            .keyManager(
                keyCertChainStream,
                privateKeyStream,
                clientDefinition.sslPrivateKeyPassword
            )

        val trustCertCollectionStream = openStream(trustCertCollectionClassPath, trustCertCollectionFile)
        if (trustCertCollectionStream !== null) {
            sslBuilder.trustManager(trustCertCollectionStream)
        }

        val clientAuthString = clientDefinition.sslClientAuth
        if (clientAuthString !== null) {
            val clientAuth = ClientAuth::class.java.enumValueOfIgnoreCase<ClientAuth>(clientAuthString)
            if (clientAuth !== ClientAuth.NONE)
                sslBuilder.clientAuth(clientAuth)
        }

        builder.sslContext(sslBuilder.build())
    }

    open fun configureShadedNettyChannelMisc(builder: NettyChannelBuilder, clientDefinition: GrpcClientDefinition) {
        val initialFlowControlWindow = clientDefinition.initialFlowControlWindow
        if (initialFlowControlWindow !== null) {
            builder.initialFlowControlWindow(initialFlowControlWindow)
        }
        val flowControlWindow = clientDefinition.flowControlWindow
        if (flowControlWindow !== null) {
            builder.flowControlWindow(flowControlWindow)
        }
        val maxMessageSize = clientDefinition.maxMessageSize
        if (maxMessageSize !== null) {
            builder.maxInboundMessageSize(maxMessageSize)
        }
        val maxHeaderListSize = clientDefinition.maxHeaderListSize
        if (maxHeaderListSize !== null) {
            builder.maxInboundMetadataSize(maxHeaderListSize)
        }
        val keepAliveTimeInNanos = clientDefinition.keepAliveTimeInNanos
        if (keepAliveTimeInNanos !== null) {
            builder.keepAliveTime(keepAliveTimeInNanos, TimeUnit.NANOSECONDS)
        }
        val keepAliveTimeoutInNanos = clientDefinition.keepAliveTimeoutInNanos
        if (keepAliveTimeoutInNanos !== null) {
            builder.keepAliveTimeout(keepAliveTimeoutInNanos, TimeUnit.NANOSECONDS)
        }
        val keepAliveWithoutCalls = clientDefinition.keepAliveWithoutCalls
        if (keepAliveWithoutCalls !== null) {
            builder.keepAliveWithoutCalls(keepAliveWithoutCalls)
        }
    }
}