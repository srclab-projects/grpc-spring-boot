package xyz.srclab.grpc.spring.boot.server

import io.grpc.ServerBuilder
import io.grpc.netty.GrpcSslContexts
import io.grpc.netty.NettyServerBuilder
import io.netty.handler.ssl.ClientAuth
import org.springframework.context.ApplicationContext
import xyz.srclab.common.base.enumValueOfIgnoreCase
import xyz.srclab.common.base.loadResource
import java.io.File
import java.io.InputStream
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.annotation.Resource
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts as ShadedGrpcSslContexts
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder as ShadedNettyServerBuilder
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth as ShadedClientAuth

open class GrpcServerBuilderConfigureHelper {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    open fun configureServices(
        builder: ServerBuilder<*>,
        serverDefinition: GrpcServerDefinition,
        serviceBuilders: Set<GrpcServiceDefinitionBuilder>,
    ) {
        for (serviceBuilder in serviceBuilders) {
            builder.addService(serviceBuilder.build())
        }
    }

    open fun configureExecutor(builder: ServerBuilder<*>, serverDefinition: GrpcServerDefinition) {
        val threadPoolBeanName = serverDefinition.threadPoolBeanName
        if (threadPoolBeanName === null) {
            return
        }
        val executor = applicationContext.getBean(threadPoolBeanName)
        if (executor !is Executor) {
            throw IllegalArgumentException("bean $threadPoolBeanName is not a Executor.")
        }
        builder.executor(executor)
    }

    open fun configureSsl(builder: NettyServerBuilder, serverDefinition: GrpcServerDefinition) {
        val keyCertChainClassPath = serverDefinition.sslCertChainClassPath
        val privateKeyClassPath = serverDefinition.sslPrivateKeyClassPath
        val trustCertCollectionClassPath = serverDefinition.sslTrustCertCollectionClassPath
        val keyCertChainFile = serverDefinition.sslCertChainFile
        val privateKeyFile = serverDefinition.sslPrivateKeyFile
        val trustCertCollectionFile = serverDefinition.sslTrustCertCollectionFile

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
            GrpcSslContexts.forServer(keyCertChainStream, privateKeyStream, serverDefinition.sslPrivateKeyPassword)

        val clientAuthString = serverDefinition.sslClientAuth
        if (clientAuthString !== null) {
            val clientAuth = ClientAuth::class.java.enumValueOfIgnoreCase<ClientAuth>(clientAuthString)
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

    open fun configureSsl(builder: ShadedNettyServerBuilder, serverDefinition: GrpcServerDefinition) {
        val keyCertChainClassPath = serverDefinition.sslCertChainClassPath
        val privateKeyClassPath = serverDefinition.sslPrivateKeyClassPath
        val trustCertCollectionClassPath = serverDefinition.sslTrustCertCollectionClassPath
        val keyCertChainFile = serverDefinition.sslCertChainFile
        val privateKeyFile = serverDefinition.sslPrivateKeyFile
        val trustCertCollectionFile = serverDefinition.sslTrustCertCollectionFile

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
                serverDefinition.sslPrivateKeyPassword
            )

        val clientAuthString = serverDefinition.sslClientAuth
        if (clientAuthString !== null) {
            val clientAuth = ShadedClientAuth::class.java.enumValueOfIgnoreCase<ShadedClientAuth>(clientAuthString)
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

    open fun configureServerMisc(builder: NettyServerBuilder, serverDefinition: GrpcServerDefinition) {
        val maxConcurrentCallsPerConnection = serverDefinition.maxConcurrentCallsPerConnection
        if (maxConcurrentCallsPerConnection !== null) {
            builder.maxConcurrentCallsPerConnection(maxConcurrentCallsPerConnection)
        }
        val initialFlowControlWindow = serverDefinition.initialFlowControlWindow
        if (initialFlowControlWindow !== null) {
            builder.initialFlowControlWindow(initialFlowControlWindow)
        }
        val flowControlWindow = serverDefinition.flowControlWindow
        if (flowControlWindow !== null) {
            builder.flowControlWindow(flowControlWindow)
        }
        val maxMessageSize = serverDefinition.maxMessageSize
        if (maxMessageSize !== null) {
            builder.maxInboundMessageSize(maxMessageSize)
        }
        val maxHeaderListSize = serverDefinition.maxHeaderListSize
        if (maxHeaderListSize !== null) {
            builder.maxInboundMetadataSize(maxHeaderListSize)
        }
        val keepAliveTimeInNanos = serverDefinition.keepAliveTimeInNanos
        if (keepAliveTimeInNanos !== null) {
            builder.keepAliveTime(keepAliveTimeInNanos, TimeUnit.NANOSECONDS)
        }
        val keepAliveTimeoutInNanos = serverDefinition.keepAliveTimeoutInNanos
        if (keepAliveTimeoutInNanos !== null) {
            builder.keepAliveTimeout(keepAliveTimeoutInNanos, TimeUnit.NANOSECONDS)
        }
        val maxConnectionIdleInNanos = serverDefinition.maxConnectionIdleInNanos
        if (maxConnectionIdleInNanos !== null) {
            builder.maxConnectionIdle(maxConnectionIdleInNanos, TimeUnit.NANOSECONDS)
        }
        val maxConnectionAgeInNanos = serverDefinition.maxConnectionAgeInNanos
        if (maxConnectionAgeInNanos !== null) {
            builder.maxConnectionAge(maxConnectionAgeInNanos, TimeUnit.NANOSECONDS)
        }
        val maxConnectionAgeGraceInNanos = serverDefinition.maxConnectionAgeGraceInNanos
        if (maxConnectionAgeGraceInNanos !== null) {
            builder.maxConnectionAgeGrace(maxConnectionAgeGraceInNanos, TimeUnit.NANOSECONDS)
        }
        val permitKeepAliveWithoutCalls = serverDefinition.permitKeepAliveWithoutCalls
        if (permitKeepAliveWithoutCalls !== null) {
            builder.permitKeepAliveWithoutCalls(permitKeepAliveWithoutCalls)
        }
        val permitKeepAliveTimeInNanos = serverDefinition.permitKeepAliveTimeInNanos
        if (permitKeepAliveTimeInNanos !== null) {
            builder.permitKeepAliveTime(permitKeepAliveTimeInNanos, TimeUnit.NANOSECONDS)
        }
    }

    open fun configureServerMisc(builder: ShadedNettyServerBuilder, serverDefinition: GrpcServerDefinition) {
        val maxConcurrentCallsPerConnection = serverDefinition.maxConcurrentCallsPerConnection
        if (maxConcurrentCallsPerConnection !== null) {
            builder.maxConcurrentCallsPerConnection(maxConcurrentCallsPerConnection)
        }
        val initialFlowControlWindow = serverDefinition.initialFlowControlWindow
        if (initialFlowControlWindow !== null) {
            builder.initialFlowControlWindow(initialFlowControlWindow)
        }
        val flowControlWindow = serverDefinition.flowControlWindow
        if (flowControlWindow !== null) {
            builder.flowControlWindow(flowControlWindow)
        }
        val maxMessageSize = serverDefinition.maxMessageSize
        if (maxMessageSize !== null) {
            builder.maxInboundMessageSize(maxMessageSize)
        }
        val maxHeaderListSize = serverDefinition.maxHeaderListSize
        if (maxHeaderListSize !== null) {
            builder.maxInboundMetadataSize(maxHeaderListSize)
        }
        val keepAliveTimeInNanos = serverDefinition.keepAliveTimeInNanos
        if (keepAliveTimeInNanos !== null) {
            builder.keepAliveTime(keepAliveTimeInNanos, TimeUnit.NANOSECONDS)
        }
        val keepAliveTimeoutInNanos = serverDefinition.keepAliveTimeoutInNanos
        if (keepAliveTimeoutInNanos !== null) {
            builder.keepAliveTimeout(keepAliveTimeoutInNanos, TimeUnit.NANOSECONDS)
        }
        val maxConnectionIdleInNanos = serverDefinition.maxConnectionIdleInNanos
        if (maxConnectionIdleInNanos !== null) {
            builder.maxConnectionIdle(maxConnectionIdleInNanos, TimeUnit.NANOSECONDS)
        }
        val maxConnectionAgeInNanos = serverDefinition.maxConnectionAgeInNanos
        if (maxConnectionAgeInNanos !== null) {
            builder.maxConnectionAge(maxConnectionAgeInNanos, TimeUnit.NANOSECONDS)
        }
        val maxConnectionAgeGraceInNanos = serverDefinition.maxConnectionAgeGraceInNanos
        if (maxConnectionAgeGraceInNanos !== null) {
            builder.maxConnectionAgeGrace(maxConnectionAgeGraceInNanos, TimeUnit.NANOSECONDS)
        }
        val permitKeepAliveWithoutCalls = serverDefinition.permitKeepAliveWithoutCalls
        if (permitKeepAliveWithoutCalls !== null) {
            builder.permitKeepAliveWithoutCalls(permitKeepAliveWithoutCalls)
        }
        val permitKeepAliveTimeInNanos = serverDefinition.permitKeepAliveTimeInNanos
        if (permitKeepAliveTimeInNanos !== null) {
            builder.permitKeepAliveTime(permitKeepAliveTimeInNanos, TimeUnit.NANOSECONDS)
        }
    }
}