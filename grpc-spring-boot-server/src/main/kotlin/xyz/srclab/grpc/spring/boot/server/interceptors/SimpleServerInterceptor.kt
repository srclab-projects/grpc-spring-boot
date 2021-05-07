package xyz.srclab.grpc.spring.boot.server.interceptors

import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.Status
import xyz.srclab.grpc.spring.boot.context.GrpcContext

interface SimpleServerInterceptor {

    @JvmDefault
    fun <ReqT : Any, RespT : Any> intercept(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    @JvmDefault
    fun <ReqT : Any, RespT : Any> onReady(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    @JvmDefault
    fun <ReqT : Any, RespT : Any> onMessage(
        message: ReqT,
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    @JvmDefault
    fun <ReqT : Any, RespT : Any> onHalfClose(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    @JvmDefault
    fun <ReqT : Any, RespT : Any> sendHeaders(
        sentHeader: Metadata,
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    @JvmDefault
    fun <ReqT : Any, RespT : Any> sendMessage(
        sentMessage: RespT,
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    @JvmDefault
    fun <ReqT : Any, RespT : Any> close(
        status: Status,
        trailers: Metadata,
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    @JvmDefault
    fun <ReqT : Any, RespT : Any> onCancel(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    @JvmDefault
    fun <ReqT : Any, RespT : Any> onComplete(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    @JvmDefault
    fun <ReqT : Any, RespT : Any> onException(
        cause: Throwable,
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }
}