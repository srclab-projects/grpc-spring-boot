package xyz.srclab.spring.boot.grpc.server

import io.grpc.internal.GrpcUtil
import io.grpc.netty.NettyServerBuilder
import java.util.concurrent.TimeUnit

open class GrpcServersProperties {
    var defaultThreadPoolBeanName: String? = null
    var servers: Map<String, GrpcServerProperties> = emptyMap()
}

open class GrpcServerProperties {
    var inProcess: Boolean = false
    var ip: String = "127.0.0.1"
    var port: Int = 6565
    var groupPatterns: List<String> = emptyList()
    var threadPoolBeanName: String? = null

    var maxConcurrentCallsPerConnection = Int.MAX_VALUE
    var flowControlWindow = NettyServerBuilder.DEFAULT_FLOW_CONTROL_WINDOW
    var maxMessageSize = GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE
    var maxHeaderListSize = GrpcUtil.DEFAULT_MAX_HEADER_LIST_SIZE
    var keepAliveTimeInNanos = GrpcUtil.DEFAULT_SERVER_KEEPALIVE_TIME_NANOS
    var keepAliveTimeoutInNanos = GrpcUtil.DEFAULT_SERVER_KEEPALIVE_TIMEOUT_NANOS
    var maxConnectionIdleInNanos = Long.MAX_VALUE
    var maxConnectionAgeInNanos = Long.MAX_VALUE
    var maxConnectionAgeGraceInNanos = Long.MAX_VALUE
    var permitKeepAliveWithoutCalls = false
    var permitKeepAliveTimeInNanos = TimeUnit.MINUTES.toNanos(5)
}