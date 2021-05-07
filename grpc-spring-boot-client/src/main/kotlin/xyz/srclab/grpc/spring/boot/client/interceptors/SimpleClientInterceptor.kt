package xyz.srclab.grpc.spring.boot.client.interceptors

import io.grpc.CallOptions
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.Status
import xyz.srclab.grpc.spring.boot.context.GrpcContext

/**
 * Simple client interceptor of which all callbacks in natural order (interceptor1 -> interceptor2).
 *
 * @see AbstractClientInterceptor
 */
interface SimpleClientInterceptor {

    fun <ReqT : Any, RespT : Any> intercept(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        context: GrpcContext,
    ) {
    }

    fun <ReqT : Any, RespT : Any> sendHeaders(
        sentHeader: Metadata,
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        context: GrpcContext,
    ) {
    }

    fun <ReqT : Any, RespT : Any> sendMessage(
        sentMessage: ReqT,
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        context: GrpcContext,
    ) {
    }

    fun <ReqT : Any, RespT : Any> onReady(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        context: GrpcContext,
    ) {
    }

    fun <ReqT : Any, RespT : Any> onHeaders(
        headers: Metadata,
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        context: GrpcContext,
    ) {
    }

    fun <ReqT : Any, RespT : Any> onMessage(
        message: RespT,
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        context: GrpcContext,
    ) {
    }

    fun <ReqT : Any, RespT : Any> onClose(
        status: Status,
        trailers: Metadata,
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        context: GrpcContext,
    ) {
    }
}