package xyz.srclab.grpc.spring.boot.server.interceptors

import io.grpc.*
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener
import xyz.srclab.grpc.spring.boot.context.GrpcContext

/**
 * Provides a skeletal implementation of [ServerInterceptor]. Execute order (assume there are twe interceptors):
 *
 * * intercept1 -> intercept2 ->
 * * onReady2 -> onReady1 ->
 * * onMessage2 -> onMessage1 ->
 * * service executing ->
 * * responseObserver.onNext ->
 * * sendHeaders1 -> sendHeaders2 ->
 * * sendMessage1 -> sendMessage2 ->
 * * responseObserver.afterOnNext ->
 * * responseObserver.onCompleted ->
 * * close1 -> close2 ->
 * * responseObserver.afterOnCompleted ->
 * * onHalfClose2 -> onHalfClose1 ->
 * * onComplete2 -> onComplete1
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

    protected open fun <ReqT : Any, RespT : Any> onException(
        cause: Throwable,
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
                try {
                    super.sendMessage(message)
                    sendMessage(message, call, headers, grpcContext)
                } catch (e: Throwable) {
                    onException(e, call, headers, grpcContext)
                }
            }

            override fun sendHeaders(sentHeaders: Metadata) {
                try {
                    super.sendHeaders(headers)
                    sendHeaders(sentHeaders, call, headers, grpcContext)
                } catch (e: Throwable) {
                    onException(e, call, headers, grpcContext)
                }
            }

            override fun close(status: Status, trailers: Metadata) {
                try {
                    super.close(status, trailers)
                    close(status, trailers, call, headers, grpcContext)
                } catch (e: Throwable) {
                    onException(e, call, headers, grpcContext)
                }
            }
        }

        class ContextualizedServerCallListener(
            delegate: ServerCall.Listener<ReqT>, private val rawContext: Context)
            : SimpleForwardingServerCallListener<ReqT>(delegate) {

            override fun onReady() {
                val previous = rawContext.attach()
                try {
                    super.onReady()
                    onReady(call, headers, grpcContext)
                } catch (e: Throwable) {
                    onException(e, call, headers, grpcContext)
                } finally {
                    rawContext.detach(previous)
                }
            }

            override fun onMessage(message: ReqT) {
                val previous = rawContext.attach()
                try {
                    super.onMessage(message)
                    onMessage(message, call, headers, grpcContext)
                } catch (e: Throwable) {
                    onException(e, call, headers, grpcContext)
                } finally {
                    rawContext.detach(previous)
                }
            }

            override fun onHalfClose() {
                val previous = rawContext.attach()
                try {
                    super.onHalfClose()
                    onHalfClose(call, headers, grpcContext)
                } catch (e: Throwable) {
                    onException(e, call, headers, grpcContext)
                } finally {
                    rawContext.detach(previous)
                }
            }

            override fun onCancel() {
                val previous = rawContext.attach()
                try {
                    super.onCancel()
                    onCancel(call, headers, grpcContext)
                } catch (e: Throwable) {
                    onException(e, call, headers, grpcContext)
                } finally {
                    rawContext.detach(previous)
                }
            }

            override fun onComplete() {
                val previous = rawContext.attach()
                try {
                    super.onComplete()
                    onComplete(call, headers, grpcContext)
                } catch (e: Throwable) {
                    onException(e, call, headers, grpcContext)
                } finally {
                    rawContext.detach(previous)
                }
            }
        }

        val rawContext = grpcContext.rawContext
        val previous = rawContext.attach()
        val delegateListener = next.startCall(delegateCall, headers)
        return try {
            ContextualizedServerCallListener(delegateListener, rawContext)
        } finally {
            rawContext.detach(previous)
        }
    }
}