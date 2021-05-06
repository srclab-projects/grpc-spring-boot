package xyz.srclab.grpc.spring.boot.client.interceptors

import io.grpc.*
import xyz.srclab.grpc.spring.boot.context.GrpcContext

/**
 * Provides a skeletal implementation of [ClientInterceptor]. Execute order (assume there are twe interceptors):
 *
 * * intercept1 -> intercept2 ->
 * * sendHeaders1 -> sendHeaders2 ->
 * * sendMessage1 -> sendMessage2 ->
 * * onHeaders1 -> onHeaders2 ->
 * * onMessage1 -> onMessage2 ->
 * * onClose2 -> onClose1
 *
 * It is recommended that using [GrpcContext] instead of using [Context] directly.
 *
 * @see GrpcContext
 */
abstract class AbstractClientInterceptor : ClientInterceptor {

    protected open fun <ReqT : Any, RespT : Any> intercept(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        context: GrpcContext,
    ) {
    }

    protected open fun <ReqT : Any, RespT : Any> sendHeaders(
        sentHeader: Metadata,
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        context: GrpcContext,
    ) {
    }

    protected open fun <ReqT : Any, RespT : Any> sendMessage(
        sentMessage: ReqT,
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        context: GrpcContext,
    ) {
    }

    protected open fun <ReqT : Any, RespT : Any> onReady(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        context: GrpcContext,
    ) {
    }

    protected open fun <ReqT : Any, RespT : Any> onHeaders(
        headers: Metadata,
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        context: GrpcContext,
    ) {
    }

    protected open fun <ReqT : Any, RespT : Any> onMessage(
        message: RespT,
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        context: GrpcContext,
    ) {
    }

    protected open fun <ReqT : Any, RespT : Any> onClose(
        status: Status,
        trailers: Metadata,
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        context: GrpcContext,
    ) {
    }

    override fun <ReqT : Any, RespT : Any> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {

        val grpcContext = GrpcContext.current()
        intercept(method, callOptions, grpcContext)

        class ContextualizedClientCallListener(
            delegate: ClientCall.Listener<RespT>,
            private val rawContext: Context
        ) : ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(delegate) {

            override fun onReady() {
                val previous = rawContext.attach()
                try {
                    super.onReady()
                    this@AbstractClientInterceptor.onReady(method, callOptions, grpcContext)
                } finally {
                    rawContext.detach(previous)
                }
            }

            override fun onHeaders(headers: Metadata) {
                val previous = rawContext.attach()
                try {
                    super.onHeaders(headers)
                    this@AbstractClientInterceptor.onHeaders(headers, method, callOptions, grpcContext)
                } finally {
                    rawContext.detach(previous)
                }
            }

            override fun onMessage(message: RespT) {
                val previous = rawContext.attach()
                try {
                    super.onMessage(message)
                    this@AbstractClientInterceptor.onMessage(message, method, callOptions, grpcContext)
                } finally {
                    rawContext.detach(previous)
                }
            }

            override fun onClose(status: Status, trailers: Metadata) {
                val previous = rawContext.attach()
                try {
                    this@AbstractClientInterceptor.onClose(status, trailers, method, callOptions, grpcContext)
                    super.onClose(status, trailers)
                } finally {
                    rawContext.detach(previous)
                }
            }
        }

        val rawContext = grpcContext.rawContext
        val previous = rawContext.attach()
        try {
            val delegateCall = next.newCall(method, callOptions)
            return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(delegateCall) {

                override fun start(responseListener: Listener<RespT>, headers: Metadata) {
                    val delegateListener = ContextualizedClientCallListener(responseListener, rawContext)
                    this@AbstractClientInterceptor.sendHeaders(headers, method, callOptions, grpcContext)
                    super.start(delegateListener, headers)
                }

                override fun sendMessage(message: ReqT) {
                    this@AbstractClientInterceptor.sendMessage(message, method, callOptions, grpcContext)
                    super.sendMessage(message)
                }
            }
        } finally {
            rawContext.detach(previous)
        }
    }
}