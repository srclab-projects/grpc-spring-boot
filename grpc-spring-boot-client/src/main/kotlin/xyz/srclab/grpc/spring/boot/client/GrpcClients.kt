@file:JvmName("GrpcServers")

package xyz.srclab.grpc.spring.boot.client

import io.grpc.Channel
import io.grpc.stub.AbstractStub
import xyz.srclab.common.base.asAny
import xyz.srclab.common.reflect.method
import java.util.concurrent.TimeUnit

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

fun GrpcClient.clientNameOrDefaultName(clientDefinitions: Map<String, GrpcClientDefinition>): String {
    val valueOrClientName = this.valueOrClientName
    if (valueOrClientName.isNotEmpty()) {
        return valueOrClientName
    }
    return clientDefinitions.keys.firstOrNull()
        ?: throw IllegalArgumentException("No gRPC client properties found.")
}

fun <S : AbstractStub<S>> Class<*>.newStub(
    channel: Channel, clientDefinition: GrpcClientDefinition? = null
): S {
    val stub: S = this.method("newStub", Channel::class.java).invoke(null, channel).asAny()
    return if (clientDefinition !== null && clientDefinition.deadlineAfterInNanos !== null) {
        stub.withDeadlineAfter(clientDefinition.deadlineAfterInNanos, TimeUnit.NANOSECONDS)
    } else {
        stub
    }
}

fun <S : AbstractStub<S>> Class<*>.newBlockingStub(
    channel: Channel,
    clientDefinition: GrpcClientDefinition? = null
): S {
    val stub: S = this.method("newBlockingStub", Channel::class.java).invoke(null, channel).asAny()
    return if (clientDefinition !== null && clientDefinition.deadlineAfterInNanos !== null) {
        stub.withDeadlineAfter(clientDefinition.deadlineAfterInNanos, TimeUnit.NANOSECONDS)
    } else {
        stub
    }
}

fun <S : AbstractStub<S>> Class<*>.newFutureStub(
    channel: Channel, clientDefinition: GrpcClientDefinition? = null
): S {
    val stub: S = this.method("newFutureStub", Channel::class.java).invoke(null, channel).asAny()
    return if (clientDefinition !== null && clientDefinition.deadlineAfterInNanos !== null) {
        stub.withDeadlineAfter(clientDefinition.deadlineAfterInNanos, TimeUnit.NANOSECONDS)
    } else {
        stub
    }
}