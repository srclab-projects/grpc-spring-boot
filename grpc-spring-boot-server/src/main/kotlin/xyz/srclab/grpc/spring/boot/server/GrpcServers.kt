@file:JvmName("GrpcServers")

package xyz.srclab.grpc.spring.boot.server

const val DEFAULT_SERVER_NAME = "default"

const val DEFAULT_GROUP_NAME = "default"

val GrpcService.valueOrServerPatterns: List<String>
    @JvmName("valueOrServerPatterns") get() = when {
        this.value.isNotEmpty() -> this.value.toList()
        this.serverPatterns.isNotEmpty() -> this.serverPatterns.toList()
        else -> emptyList()
    }

val GrpcServerInterceptor.valueOrServicePatterns: List<String>
    @JvmName("valueOrServicePatterns") get() = when {
        this.value.isNotEmpty() -> this.value.toList()
        this.servicePatterns.isNotEmpty() -> this.servicePatterns.toList()
        else -> emptyList()
    }