@file:JvmName("GrpcServers")

package xyz.srclab.grpc.spring.boot.client

import io.grpc.Channel
import io.grpc.stub.AbstractStub
import xyz.srclab.common.base.asAny
import xyz.srclab.common.reflect.method

val GrpcClient.valueOrClientName: String
    @JvmName("valueOrClientName") get() = when {
        this.value.isNotEmpty() -> this.value
        this.clientName.isNotEmpty() -> this.clientName
        else -> ""
    }

val GrpcClientInterceptor.valueOrClientPatterns: List<String>
    @JvmName("valueOrClientPatterns") get() = when {
        this.value.isNotEmpty() -> this.value.toList()
        this.clientPatterns.isNotEmpty() -> this.clientPatterns.toList()
        else -> emptyList()
    }

fun GrpcClient.clientNameOrDefaultName(clientsProperties: GrpcClientsProperties): String {
    val valueOrClientName = this.valueOrClientName
    if (valueOrClientName.isNotEmpty()) {
        return valueOrClientName
    }
    return clientsProperties.clients.keys.firstOrNull()
        ?: throw IllegalArgumentException("No gRPC client properties found.")
}

fun <S : AbstractStub<S>> Class<*>.newStub(channel: Channel): S {
    return this.method("newStub", Channel::class.java).invoke(null, channel).asAny()
}

fun <S : AbstractStub<S>> Class<*>.newBlockingStub(channel: Channel): S {
    return this.method("newBlockingStub", Channel::class.java).invoke(null, channel).asAny()
}

fun <S : AbstractStub<S>> Class<*>.newFutureStub(channel: Channel): S {
    return this.method("newFutureStub", Channel::class.java).invoke(null, channel).asAny()
}