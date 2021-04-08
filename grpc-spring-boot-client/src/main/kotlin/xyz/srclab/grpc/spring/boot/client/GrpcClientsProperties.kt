package xyz.srclab.grpc.spring.boot.client

import io.grpc.internal.GrpcUtil
import io.grpc.netty.NettyServerBuilder
import xyz.srclab.common.collect.toImmutableSet
import java.util.concurrent.TimeUnit

open class GrpcClientsProperties {
    var defaults: GrpcClientProperties? = null
    var servers: Map<String, GrpcClientProperties> = emptyMap()
}

open class GrpcClientProperties {
    var inProcess: Boolean? = null
    var ip: String? = null
    var port: Int? = null
    var groupPatterns: List<String>? = null
    var threadPoolBeanName: String? = null

    var maxConcurrentCallsPerConnection: Int? = null
    var flowControlWindow: Int? = null
    var maxMessageSize: Int? = null
    var maxHeaderListSize: Int? = null
    var keepAliveTimeInNanos: Long? = null
    var keepAliveTimeoutInNanos: Long? = null
    var maxConnectionIdleInNanos: Long? = null
    var maxConnectionAgeInNanos: Long? = null
    var maxConnectionAgeGraceInNanos: Long? = null
    var permitKeepAliveWithoutCalls: Boolean? = null
    var permitKeepAliveTimeInNanos: Long? = null

    var sslCertChainClassPath: String? = null
    var sslPrivateKeyClassPath: String? = null
    var sslTrustCertCollectionClassPath: String? = null
    var sslCertChainFile: String? = null
    var sslPrivateKeyFile: String? = null
    var sslTrustCertCollectionFile: String? = null
    var sslPrivateKeyPassword: String? = null
    var sslClientAuth: String? = null
}

open class GrpcClientDefinition(
    val name: String,
    _inProcess: Boolean?,
    _ip: String?,
    _port: Int?,
    _groupPatterns: List<String>?,
    _threadPoolBeanName: String?,

    _maxConcurrentCallsPerConnection: Int?,
    _flowControlWindow: Int?,
    _maxMessageSize: Int?,
    _maxHeaderListSize: Int?,
    _keepAliveTimeInNanos: Long?,
    _keepAliveTimeoutInNanos: Long?,
    _maxConnectionIdleInNanos: Long?,
    _maxConnectionAgeInNanos: Long?,
    _maxConnectionAgeGraceInNanos: Long?,
    _permitKeepAliveWithoutCalls: Boolean?,
    _permitKeepAliveTimeInNanos: Long?,

    _sslCertChainClassPath: String?,
    _sslPrivateKeyClassPath: String?,
    _sslTrustCertCollectionClassPath: String?,
    _sslCertChainFile: String?,
    _sslPrivateKeyFile: String?,
    _sslTrustCertCollectionFile: String?,
    _sslPrivateKeyPassword: String?,
    _sslClientAuth: String?,
) {
    val inProcess: Boolean = _inProcess ?: false
    val ip: String = _ip ?: "127.0.0.1"
    val port: Int = _port ?: 6565
    val groupPatterns: List<String> = _groupPatterns ?: emptyList()
    val threadPoolBeanName: String? = _threadPoolBeanName

    val maxConcurrentCallsPerConnection: Int = _maxConcurrentCallsPerConnection ?: Int.MAX_VALUE
    val flowControlWindow: Int = _flowControlWindow ?: NettyServerBuilder.DEFAULT_FLOW_CONTROL_WINDOW
    val maxMessageSize: Int = _maxMessageSize ?: GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE
    val maxHeaderListSize: Int = _maxHeaderListSize ?: GrpcUtil.DEFAULT_MAX_HEADER_LIST_SIZE
    val keepAliveTimeInNanos: Long = _keepAliveTimeInNanos ?: GrpcUtil.DEFAULT_SERVER_KEEPALIVE_TIME_NANOS
    val keepAliveTimeoutInNanos: Long = _keepAliveTimeoutInNanos ?: GrpcUtil.DEFAULT_SERVER_KEEPALIVE_TIMEOUT_NANOS
    val maxConnectionIdleInNanos: Long = _maxConnectionIdleInNanos ?: Long.MAX_VALUE
    val maxConnectionAgeInNanos: Long = _maxConnectionAgeInNanos ?: Long.MAX_VALUE
    val maxConnectionAgeGraceInNanos: Long = _maxConnectionAgeGraceInNanos ?: Long.MAX_VALUE
    val permitKeepAliveWithoutCalls: Boolean = _permitKeepAliveWithoutCalls ?: false
    val permitKeepAliveTimeInNanos: Long = _permitKeepAliveTimeInNanos ?: TimeUnit.MINUTES.toNanos(5)

    val sslCertChainClassPath: String? = _sslCertChainClassPath
    val sslPrivateKeyClassPath: String? = _sslPrivateKeyClassPath
    val sslTrustCertCollectionClassPath: String? = _sslTrustCertCollectionClassPath
    val sslCertChainFile: String? = _sslCertChainFile
    val sslPrivateKeyFile: String? = _sslPrivateKeyFile
    val sslTrustCertCollectionFile: String? = _sslTrustCertCollectionFile
    val sslPrivateKeyPassword: String? = _sslPrivateKeyPassword

    /**
     * Enum with case-ignore: none, optional, require.
     */
    val sslClientAuth: String? = _sslClientAuth
}

fun GrpcClientsProperties.toDefinitions(): Set<GrpcClientDefinition> {
    return this.servers.entries.map { this.getServerDefinition(it.key) }.toImmutableSet()
}

private fun GrpcClientsProperties.getServerDefinition(name: String): GrpcClientDefinition {
    val defaults = this.defaults
    val properties = this.servers[name] ?: throw IllegalArgumentException("Server properties $name not found")
    if (defaults === null) {
        return GrpcClientDefinition(
            name,
            properties.inProcess,
            properties.ip,
            properties.port,
            properties.groupPatterns,
            properties.threadPoolBeanName,
            properties.maxConcurrentCallsPerConnection,
            properties.flowControlWindow,
            properties.maxMessageSize,
            properties.maxHeaderListSize,
            properties.keepAliveTimeInNanos,
            properties.keepAliveTimeoutInNanos,
            properties.maxConnectionIdleInNanos,
            properties.maxConnectionAgeInNanos,
            properties.maxConnectionAgeGraceInNanos,
            properties.permitKeepAliveWithoutCalls,
            properties.permitKeepAliveTimeInNanos,
            properties.sslCertChainClassPath,
            properties.sslPrivateKeyClassPath,
            properties.sslTrustCertCollectionClassPath,
            properties.sslCertChainFile,
            properties.sslPrivateKeyFile,
            properties.sslTrustCertCollectionFile,
            properties.sslPrivateKeyPassword,
            properties.sslClientAuth,
        )
    } else {
        return GrpcClientDefinition(
            name,
            properties.inProcess ?: defaults.inProcess,
            properties.ip ?: defaults.ip,
            properties.port ?: defaults.port,
            properties.groupPatterns ?: defaults.groupPatterns,
            properties.threadPoolBeanName ?: defaults.threadPoolBeanName,
            properties.maxConcurrentCallsPerConnection ?: defaults.maxConcurrentCallsPerConnection,
            properties.flowControlWindow ?: defaults.flowControlWindow,
            properties.maxMessageSize ?: defaults.maxMessageSize,
            properties.maxHeaderListSize ?: defaults.maxHeaderListSize,
            properties.keepAliveTimeInNanos ?: defaults.keepAliveTimeInNanos,
            properties.keepAliveTimeoutInNanos ?: defaults.keepAliveTimeoutInNanos,
            properties.maxConnectionIdleInNanos ?: defaults.maxConnectionIdleInNanos,
            properties.maxConnectionAgeInNanos ?: defaults.maxConnectionAgeInNanos,
            properties.maxConnectionAgeGraceInNanos ?: defaults.maxConnectionAgeGraceInNanos,
            properties.permitKeepAliveWithoutCalls ?: defaults.permitKeepAliveWithoutCalls,
            properties.permitKeepAliveTimeInNanos ?: defaults.permitKeepAliveTimeInNanos,
            properties.sslCertChainClassPath ?: defaults.sslCertChainClassPath,
            properties.sslPrivateKeyClassPath ?: defaults.sslPrivateKeyClassPath,
            properties.sslTrustCertCollectionClassPath ?: defaults.sslTrustCertCollectionClassPath,
            properties.sslCertChainFile ?: defaults.sslCertChainFile,
            properties.sslPrivateKeyFile ?: defaults.sslPrivateKeyFile,
            properties.sslTrustCertCollectionFile ?: defaults.sslTrustCertCollectionFile,
            properties.sslPrivateKeyPassword ?: defaults.sslPrivateKeyPassword,
            properties.sslClientAuth ?: defaults.sslClientAuth,
        )
    }
}