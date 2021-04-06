package xyz.srclab.spring.boot.grpc.server

open class GrpcServersProperties {
    var defaultThreadPoolBeanName: String? = null
    var servers: Map<String, GrpcServerProperties> = emptyMap()
}

open class GrpcServerProperties {
    var name: String = DEFAULT_SERVER_NAME
    var inProcess: Boolean = false
    var ip: String = "127.0.0.1"
    var port: Int = 6565
    var groupPatterns: List<String> = emptyList()
    var threadPoolBeanName: String? = null
}