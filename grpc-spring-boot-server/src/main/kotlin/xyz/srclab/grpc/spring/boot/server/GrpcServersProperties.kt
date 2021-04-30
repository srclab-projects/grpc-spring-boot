package xyz.srclab.grpc.spring.boot.server

import xyz.srclab.common.collect.toImmutableSet

open class GrpcServersProperties {
    var defaults: GrpcServerProperties? = null
    var servers: Map<String, GrpcServerProperties> = emptyMap()

    /**
     * Whether gRPC bean should be annotated by gRPC annotation ([GrpcService] and [GrpcServerInterceptor]).
     *
     * Default is false.
     */
    var needGrpcAnnotation: Boolean? = false
}

open class GrpcServerProperties {
    var inProcess: Boolean? = null
    var useShaded: Boolean? = null
    var host: String? = null
    var port: Int? = null

    var threadPoolBeanName: String? = null
    var maxConcurrentCallsPerConnection: Int? = null
    var initialFlowControlWindow: Int? = null
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

    /**
     * Enum with case-ignore: none, optional, require.
     */
    var sslClientAuth: String? = null
}

open class GrpcServersConfig(
    _needGrpcAnnotation: Boolean?,
) {

    /**
     * Whether gRPC bean should be annotated by gRPC annotation ([GrpcService] and [GrpcServerInterceptor]).
     *
     * Default is false.
     */
    val needGrpcAnnotation: Boolean = _needGrpcAnnotation ?: false
}

fun GrpcServersProperties.toServersConfig(): GrpcServersConfig {
    return GrpcServersConfig(this.needGrpcAnnotation)
}

open class GrpcServerConfig(
    val name: String,
    _inProcess: Boolean?,
    _useShaded: Boolean?,
    _host: String?,
    _port: Int?,

    _threadPoolBeanName: String?,
    _maxConcurrentCallsPerConnection: Int?,
    _initialFlowControlWindow: Int?,
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
    val useShaded: Boolean = _useShaded ?: false
    val host: String = _host ?: "127.0.0.1"
    val port: Int = _port ?: 6565

    val threadPoolBeanName: String? = _threadPoolBeanName
    val maxConcurrentCallsPerConnection: Int? = _maxConcurrentCallsPerConnection
    val initialFlowControlWindow: Int? = _initialFlowControlWindow
    val flowControlWindow: Int? = _flowControlWindow
    val maxMessageSize: Int? = _maxMessageSize
    val maxHeaderListSize: Int? = _maxHeaderListSize
    val keepAliveTimeInNanos: Long? = _keepAliveTimeInNanos
    val keepAliveTimeoutInNanos: Long? = _keepAliveTimeoutInNanos
    val maxConnectionIdleInNanos: Long? = _maxConnectionIdleInNanos
    val maxConnectionAgeInNanos: Long? = _maxConnectionAgeInNanos
    val maxConnectionAgeGraceInNanos: Long? = _maxConnectionAgeGraceInNanos
    val permitKeepAliveWithoutCalls: Boolean? = _permitKeepAliveWithoutCalls
    val permitKeepAliveTimeInNanos: Long? = _permitKeepAliveTimeInNanos

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

fun GrpcServersProperties.toServerConfigs(): Set<GrpcServerConfig> {
    return this.servers.entries.map { getServerDefinition(it.key) }.toImmutableSet()
}

private fun GrpcServersProperties.getServerDefinition(name: String): GrpcServerConfig {
    val defaults = this.defaults
    val properties = this.servers[name] ?: throw IllegalArgumentException("Server properties $name not found")
    if (defaults === null) {
        return GrpcServerConfig(
            name,
            properties.inProcess,
            properties.useShaded,
            properties.host,
            properties.port,
            properties.threadPoolBeanName,
            properties.maxConcurrentCallsPerConnection,
            properties.initialFlowControlWindow,
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
        return GrpcServerConfig(
            name,
            properties.inProcess ?: defaults.inProcess,
            properties.useShaded ?: defaults.useShaded,
            properties.host ?: defaults.host,
            properties.port ?: defaults.port,
            properties.threadPoolBeanName ?: defaults.threadPoolBeanName,
            properties.maxConcurrentCallsPerConnection ?: defaults.maxConcurrentCallsPerConnection,
            properties.initialFlowControlWindow ?: defaults.initialFlowControlWindow,
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