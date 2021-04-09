package xyz.srclab.grpc.spring.boot.server

import io.grpc.ServerBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder
import org.springframework.context.ApplicationContext
import xyz.srclab.common.base.enumValueOfIgnoreCase
import xyz.srclab.common.base.loadResource
import java.io.File
import java.io.InputStream
import java.util.concurrent.Executor
import javax.annotation.Resource

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

        val sslBuilder =
            SslContextBuilder.forServer(keyCertChainStream, privateKeyStream, serverDefinition.sslPrivateKeyPassword)

        val trustCertCollectionStream = openStream(trustCertCollectionClassPath, trustCertCollectionFile)
        if (trustCertCollectionStream !== null) {
            sslBuilder.trustManager(trustCertCollectionStream)
        }

        val clientAuthString = serverDefinition.sslClientAuth
        if (clientAuthString !== null) {
            val clientAuth = ClientAuth::class.java.enumValueOfIgnoreCase<ClientAuth>(clientAuthString)
            if (clientAuth !== ClientAuth.NONE)
                sslBuilder.clientAuth(clientAuth)
        }

        builder.sslContext(sslBuilder.build())
    }
}