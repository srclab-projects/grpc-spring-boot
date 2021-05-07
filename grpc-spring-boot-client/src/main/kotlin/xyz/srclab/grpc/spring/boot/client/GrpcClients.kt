@file:JvmName("GrpcServers")

package xyz.srclab.grpc.spring.boot.client

import io.grpc.Channel
import io.grpc.stub.AbstractAsyncStub
import io.grpc.stub.AbstractBlockingStub
import io.grpc.stub.AbstractFutureStub
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

@JvmOverloads
fun <S : AbstractStub<S>> Class<*>.newStub(channel: Channel, clientConfig: GrpcClientConfig? = null): S {
    val grpcClass = this.declaringClass
    return when {
        AbstractAsyncStub::class.java.isAssignableFrom(this) -> grpcClass.newAsyncStub(channel, clientConfig)
        AbstractBlockingStub::class.java.isAssignableFrom(this) -> grpcClass.newBlockingStub(channel, clientConfig)
        AbstractFutureStub::class.java.isAssignableFrom(this) -> grpcClass.newFutureStub(channel, clientConfig)
        else -> throw IllegalStateException("Not stub class: $this")
    }
}

@JvmOverloads
fun <S : AbstractStub<S>> Class<*>.newAsyncStub(
    channel: Channel, clientConfig: GrpcClientConfig? = null
): S {
    val stub: S = this.method("newStub", Channel::class.java).invoke(null, channel).asAny()
    return if (clientConfig !== null && clientConfig.deadlineAfterInNanos !== null) {
        stub.withDeadlineAfter(clientConfig.deadlineAfterInNanos, TimeUnit.NANOSECONDS)
    } else {
        stub
    }
}

@JvmOverloads
fun <S : AbstractStub<S>> Class<*>.newBlockingStub(
    channel: Channel,
    clientConfig: GrpcClientConfig? = null
): S {
    val stub: S = this.method("newBlockingStub", Channel::class.java).invoke(null, channel).asAny()
    return if (clientConfig !== null && clientConfig.deadlineAfterInNanos !== null) {
        stub.withDeadlineAfter(clientConfig.deadlineAfterInNanos, TimeUnit.NANOSECONDS)
    } else {
        stub
    }
}

@JvmOverloads
fun <S : AbstractStub<S>> Class<*>.newFutureStub(
    channel: Channel, clientConfig: GrpcClientConfig? = null
): S {
    val stub: S = this.method("newFutureStub", Channel::class.java).invoke(null, channel).asAny()
    return if (clientConfig !== null && clientConfig.deadlineAfterInNanos !== null) {
        stub.withDeadlineAfter(clientConfig.deadlineAfterInNanos, TimeUnit.NANOSECONDS)
    } else {
        stub
    }
}