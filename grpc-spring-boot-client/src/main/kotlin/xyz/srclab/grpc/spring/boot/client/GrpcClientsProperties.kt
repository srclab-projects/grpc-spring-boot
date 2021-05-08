package xyz.srclab.grpc.spring.boot.client

import io.grpc.ClientInterceptor
import xyz.srclab.common.collect.map

open class GrpcClientsProperties {
    var defaults: GrpcClientProperties? = null
    var clients: Map<String, GrpcClientProperties> = emptyMap()

    /**
     * Whether gRPC bean [ClientInterceptor] should be annotated by gRPC annotation ([GrpcClientInterceptor]).
     *
     * Default is false.
     */
    var needGrpcAnnotation: Boolean? = false
}

open class GrpcClientProperties {
    var inProcess: Boolean? = null
    var useShaded: Boolean? = null
    var targets: String? = null
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
     * Including:
     *
     * * round_robin
     * * pick_first
     *
     * Default is round_robin.
     */
    var loadBalancingPolicy: String? = null

    var sslCertChainClassPath: String? = null
    var sslPrivateKeyClassPath: String? = null
    var sslTrustCertCollectionClassPath: String? = null
    var sslCertChainFile: String? = null
    var sslPrivateKeyFile: String? = null
    var sslTrustCertCollectionFile: String? = null
    var sslPrivateKeyPassword: String? = null

    /**
     * Auth enum with case-ignore:
     *
     * * none
     * * optional
     * * require
     *
     * Default is none.
     */
    var sslClientAuth: String? = null
}

open class GrpcClientsConfig(
    _needGrpcAnnotation: Boolean?,
) {

    /**
     * Whether gRPC bean [ClientInterceptor] should be annotated by gRPC annotation ([GrpcClientInterceptor]).
     *
     * Default is false.
     */
    val needGrpcAnnotation: Boolean = _needGrpcAnnotation ?: false
}

open class GrpcClientConfig(
    val name: String,
    _inProcess: Boolean?,
    _useShaded: Boolean?,
    _target: String?,
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
    val useShaded: Boolean = _useShaded ?: false
    val target: String = _target ?: "127.0.0.1"
    val threadPoolBeanName: String? = _threadPoolBeanName

    val initialFlowControlWindow: Int? = _initialFlowControlWindow
    val flowControlWindow: Int? = _flowControlWindow
    val maxMessageSize: Int? = _maxMessageSize
    val maxHeaderListSize: Int? = _maxHeaderListSize
    val keepAliveTimeInNanos: Long? = _keepAliveTimeInNanos
    val keepAliveTimeoutInNanos: Long? = _keepAliveTimeoutInNanos
    val keepAliveWithoutCalls: Boolean? = _keepAliveWithoutCalls
    val deadlineAfterInNanos: Long? = _deadlineAfterInNanos

    /**
     * Including:
     *
     * * round_robin
     * * pick_first
     *
     * Default is round_robin.
     */
    val loadBalancingPolicy: String = _loadBalancingPolicy ?: ROUND_ROBIN_POLICY

    val sslCertChainClassPath: String? = _sslCertChainClassPath
    val sslPrivateKeyClassPath: String? = _sslPrivateKeyClassPath
    val sslTrustCertCollectionClassPath: String? = _sslTrustCertCollectionClassPath
    val sslCertChainFile: String? = _sslCertChainFile
    val sslPrivateKeyFile: String? = _sslPrivateKeyFile
    val sslTrustCertCollectionFile: String? = _sslTrustCertCollectionFile
    val sslPrivateKeyPassword: String? = _sslPrivateKeyPassword

    /**
     * Auth enum with case-ignore:
     *
     * * none
     * * optional
     * * require
     *
     * Default is none.
     */
    val sslClientAuth: String? = _sslClientAuth
}

internal fun GrpcClientsProperties.toClientsConfig(): GrpcClientsConfig {
    return GrpcClientsConfig(this.needGrpcAnnotation)
}

internal fun GrpcClientsProperties.toClientConfigs(): Map<String, GrpcClientConfig> {
    return this.clients.map { name, _ ->
        name to toClientConfig(name)
    }
}

private fun GrpcClientsProperties.toClientConfig(name: String): GrpcClientConfig {
    val defaults = this.defaults
    val properties = this.clients[name] ?: throw IllegalArgumentException("Server properties $name not found")
    if (defaults === null) {
        return GrpcClientConfig(
            name,
            properties.inProcess,
            properties.useShaded,
            properties.targets,
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
        return GrpcClientConfig(
            name,
            properties.inProcess ?: defaults.inProcess,
            properties.useShaded ?: defaults.useShaded,
            properties.targets ?: defaults.targets,
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