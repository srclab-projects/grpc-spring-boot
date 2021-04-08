@file:JvmName("GrpcServers")

package xyz.srclab.grpc.spring.boot.server

const val DEFAULT_SERVER_NAME = "default"

const val DEFAULT_GROUP_NAME = "default"

val GrpcService.valueOrGroup: List<String>
    @JvmName("valueOrGroup") get() = when {
        this.value.isNotEmpty() -> this.value.toList()
        this.group.isNotEmpty() -> this.group.toList()
        else -> emptyList()
    }

val GrpcServerInterceptor.valueOrGroupPattern: List<String>
    @JvmName("valueOrGroup") get() = when {
        this.value.isNotEmpty() -> this.value.toList()
        this.groupPattern.isNotEmpty() -> this.groupPattern.toList()
        else -> emptyList()
    }