@file:JvmName("GrpcServers")

package xyz.srclab.grpc.spring.boot.server

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