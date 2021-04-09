package xyz.srclab.grpc.spring.boot.server

import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder
import xyz.srclab.common.base.enumValueOfIgnoreCase
import xyz.srclab.common.base.loadResource
import java.io.File
import java.io.InputStream

open class DefaultGrpcServerSslFactory : GrpcServerSslFactory {

    override fun create(serverDefinition: GrpcServerDefinition): SslContext? {
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
            return null
        }
        val privateKeyStream = openStream(privateKeyClassPath, privateKeyFile)
        if (privateKeyStream === null) {
            return null
        }

        val builder =
            SslContextBuilder.forServer(keyCertChainStream, privateKeyStream, serverDefinition.sslPrivateKeyPassword)

        val trustCertCollectionStream = openStream(trustCertCollectionClassPath, trustCertCollectionFile)
        if (trustCertCollectionStream !== null) {
            builder.trustManager(trustCertCollectionStream)
        }

        val clientAuthString = serverDefinition.sslClientAuth
        if (clientAuthString !== null) {
            val clientAuth = ClientAuth::class.java.enumValueOfIgnoreCase<ClientAuth>(clientAuthString)
            if (clientAuth !== ClientAuth.NONE)
                builder.clientAuth(clientAuth)
        }

        return builder.build()
    }
}