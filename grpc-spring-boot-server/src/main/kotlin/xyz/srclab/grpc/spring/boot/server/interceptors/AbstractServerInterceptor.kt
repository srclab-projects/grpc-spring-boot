package xyz.srclab.grpc.spring.boot.server.interceptors

import io.grpc.*
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener
import xyz.srclab.grpc.spring.boot.context.GrpcContext

/**
 * Provides a skeletal implementation of [ServerInterceptor]. Execute order (assume there are twe interceptors):
 *
 * * intercept1 -> intercept2 ->
 * * onReady1 -> onReady2 ->
 * * onMessage1 -> onMessage2 ->
 * * onHalfClose1 -> onHalfClose2 ->
 * * service executing ->
 * * responseObserver.onNext ->
 * * sendHeaders2 -> sendHeaders1 ->
 * * sendMessage2 -> sendMessage1 ->
 * * responseObserver.afterOnNext ->
 * * responseObserver.onCompleted ->
 * * close2 -> close1 ->
 * * responseObserver.afterOnCompleted ->
 * * onComplete1 -> onComplete2
 *
 * It is recommended that using [GrpcContext] instead of using [Context] directly.
 *
 * @see GrpcContext
 */
abstract class AbstractServerInterceptor : ServerInterceptor {

    protected open fun <ReqT : Any, RespT : Any> intercept(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    protected open fun <ReqT : Any, RespT : Any> onReady(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    protected open fun <ReqT : Any, RespT : Any> onMessage(
        message: ReqT,
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    protected open fun <ReqT : Any, RespT : Any> onHalfClose(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    protected open fun <ReqT : Any, RespT : Any> sendHeaders(
        sentHeader: Metadata,
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    protected open fun <ReqT : Any, RespT : Any> sendMessage(
        sentMessage: RespT,
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    protected open fun <ReqT : Any, RespT : Any> close(
        status: Status,
        trailers: Metadata,
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    protected open fun <ReqT : Any, RespT : Any> onCancel(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    protected open fun <ReqT : Any, RespT : Any> onComplete(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        context: GrpcContext,
    ) {
    }

    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {

        val grpcContext = GrpcContext.current()
        intercept(call, headers, grpcContext)

        val delegateCall = object : SimpleForwardingServerCall<ReqT, RespT>(call) {

            override fun sendMessage(message: RespT) {
                super.sendMessage(message)
                this@AbstractServerInterceptor.sendMessage(message, call, headers, grpcContext)
            }

            override fun sendHeaders(sentHeaders: Metadata) {
                super.sendHeaders(headers)
                this@AbstractServerInterceptor.sendHeaders(sentHeaders, call, headers, grpcContext)
            }

            override fun close(status: Status, trailers: Metadata) {
                super.close(status, trailers)
                this@AbstractServerInterceptor.close(status, trailers, call, headers, grpcContext)
            }
        }

        class ContextualizedServerCallListener(
            delegate: ServerCall.Listener<ReqT>, private val rawContext: Context)
            : SimpleForwardingServerCallListener<ReqT>(delegate) {

            override fun onReady() {
                val previous = rawContext.attach()
                try {
                    super.onReady()
                    this@AbstractServerInterceptor.onReady(call, headers, grpcContext)
                } finally {
                    rawContext.detach(previous)
                }
            }

            override fun onMessage(message: ReqT) {
                val previous = rawContext.attach()
                try {
                    super.onMessage(message)
                    this@AbstractServerInterceptor.onMessage(message, call, headers, grpcContext)
                } finally {
                    rawContext.detach(previous)
                }
            }

            override fun onHalfClose() {
                val previous = rawContext.attach()
                try {
                    super.onHalfClose()
                    this@AbstractServerInterceptor.onHalfClose(call, headers, grpcContext)
                } finally {
                    rawContext.detach(previous)
                }
            }

            override fun onCancel() {
                val previous = rawContext.attach()
                try {
                    super.onCancel()
                    this@AbstractServerInterceptor.onCancel(call, headers, grpcContext)
                } finally {
                    rawContext.detach(previous)
                }
            }

            override fun onComplete() {
                val previous = rawContext.attach()
                try {
                    super.onComplete()
                    this@AbstractServerInterceptor.onComplete(call, headers, grpcContext)
                } finally {
                    rawContext.detach(previous)
                }
            }
        }

        val rawContext = grpcContext.rawContext
        val previous = rawContext.attach()
        return try {
            ContextualizedServerCallListener(next.startCall(delegateCall, headers), rawContext)
        } finally {
            rawContext.detach(previous)
        }
    }
}