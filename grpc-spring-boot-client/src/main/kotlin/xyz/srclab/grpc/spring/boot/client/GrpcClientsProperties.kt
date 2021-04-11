package xyz.srclab.grpc.spring.boot.client

import xyz.srclab.common.collect.map

open class GrpcClientsProperties {
    var defaults: GrpcClientProperties? = null
    var clients: Map<String, GrpcClientProperties> = emptyMap()
}

open class GrpcClientProperties {
    var inProcess: Boolean? = null
    var targets: String? = null
    var groupPatterns: List<String>? = null
    var threadPoolBeanName: String? = null

    var initialFlowControlWindow: Int? = null
    var flowControlWindow: Int? = null
    var maxMessageSize: Int? = null
    var maxHeaderListSize: Int? = null
    var keepAliveTimeInNanos: Long? = null
    var keepAliveTimeoutInNanos: Long? = null
    var keepAliveWithoutCalls: Boolean? = null
    val deadlineAfterInNanos: Long? = null

    /**
     * Default: pick_first;
     * Others: round_robin.
     */
    var loadBalancingPolicy: String? = null

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
    _target: String?,
    _groupPatterns: List<String>?,
    _threadPoolBeanName: String?,

    _initialFlowControlWindow: Int?,
    _flowControlWindow: Int?,
    _maxMessageSize: Int?,
    _maxHeaderListSize: Int?,
    _keepAliveTimeInNanos: Long?,
    _keepAliveTimeoutInNanos: Long?,
    _keepAliveWithoutCalls: Boolean?,
    _deadlineAfterInNanos: Long?,

    _loadBalancingPolicy: String?,

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
    val target: String = _target ?: "127.0.0.1"
    val groupPatterns: List<String> = _groupPatterns ?: emptyList()
    val threadPoolBeanName: String? = _threadPoolBeanName

    val initialFlowControlWindow: Int? = _initialFlowControlWindow
    val flowControlWindow: Int? = _flowControlWindow
    val maxMessageSize: Int? = _maxMessageSize
    val maxHeaderListSize: Int? = _maxHeaderListSize
    val keepAliveTimeInNanos: Long? = _keepAliveTimeInNanos
    val keepAliveTimeoutInNanos: Long? = _keepAliveTimeoutInNanos
    val keepAliveWithoutCalls: Boolean? = _keepAliveWithoutCalls
    val deadlineAfterInNanos: Long? = _deadlineAfterInNanos

    val loadBalancingPolicy: String? = _loadBalancingPolicy

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

fun GrpcClientsProperties.toDefinitions(): Map<String, GrpcClientDefinition> {
    return this.clients.map { name, _ ->
        name to getClientDefinition(name)
    }
}

private fun GrpcClientsProperties.getClientDefinition(name: String): GrpcClientDefinition {
    val defaults = this.defaults
    val properties = this.clients[name] ?: throw IllegalArgumentException("Server properties $name not found")
    if (defaults === null) {
        return GrpcClientDefinition(
            name,
            properties.inProcess,
            properties.targets,
            properties.groupPatterns,
            properties.threadPoolBeanName,
            properties.initialFlowControlWindow,
            properties.flowControlWindow,
            properties.maxMessageSize,
            properties.maxHeaderListSize,
            properties.keepAliveTimeInNanos,
            properties.keepAliveTimeoutInNanos,
            properties.keepAliveWithoutCalls,
            properties.deadlineAfterInNanos,
            properties.loadBalancingPolicy,
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
            properties.targets ?: defaults.targets,
            properties.groupPatterns ?: defaults.groupPatterns,
            properties.threadPoolBeanName ?: defaults.threadPoolBeanName,
            properties.initialFlowControlWindow ?: defaults.initialFlowControlWindow,
            properties.flowControlWindow ?: defaults.flowControlWindow,
            properties.maxMessageSize ?: defaults.maxMessageSize,
            properties.maxHeaderListSize ?: defaults.maxHeaderListSize,
            properties.keepAliveTimeInNanos ?: defaults.keepAliveTimeInNanos,
            properties.keepAliveTimeoutInNanos ?: defaults.keepAliveTimeoutInNanos,
            properties.keepAliveWithoutCalls ?: defaults.keepAliveWithoutCalls,
            properties.deadlineAfterInNanos ?: defaults.deadlineAfterInNanos,
            properties.loadBalancingPolicy ?: defaults.loadBalancingPolicy,
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