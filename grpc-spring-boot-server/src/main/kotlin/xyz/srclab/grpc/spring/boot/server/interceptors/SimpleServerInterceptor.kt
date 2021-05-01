package xyz.srclab.grpc.spring.boot.server.interceptors

import io.grpc.*
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall

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
 */
interface SimpleServerInterceptor : ServerInterceptor {

    @JvmDefault
    fun <ReqT : Any, RespT : Any> intercept(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): Context? {
        return null
    }

    @JvmDefault
    fun onReady(requestHeaders: Metadata) {
    }

    @JvmDefault
    fun <ReqT : Any> onMessage(message: ReqT, requestHeaders: Metadata) {
    }

    @JvmDefault
    fun onHalfClose(requestHeaders: Metadata) {
    }

    @JvmDefault
    fun onCancel(requestHeaders: Metadata) {
    }

    @JvmDefault
    fun sendHeaders(headers: Metadata) {
    }

    @JvmDefault
    fun <RespT : Any> sendMessage(message: RespT) {
    }

    @JvmDefault
    fun close(status: Status, trailers: Metadata) {
    }

    @JvmDefault
    fun onComplete(requestHeaders: Metadata) {
    }

    @JvmDefault
    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {

val context = intercept(call, headers, next)
val delegatedCall = object : SimpleForwardingServerCall<ReqT, RespT>(call) {

    override fun sendMessage(message: RespT) {
        this@SimpleServerInterceptor.sendMessage(message)
        super.sendMessage(message)
    }

    override fun sendHeaders(headers: Metadata) {
        this@SimpleServerInterceptor.sendHeaders(headers)
        super.sendHeaders(headers)
    }

    override fun close(status: Status, trailers: Metadata) {
        this@SimpleServerInterceptor.close(status, trailers)
        super.close(status, trailers)
    }
}
val delegatedListener: ServerCall.Listener<ReqT> =
    if (context === null)
        next.startCall(delegatedCall, headers)
    else
        Contexts.interceptCall(context, delegatedCall, headers, next)

return object : ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(delegatedListener) {

    override fun onMessage(message: ReqT) {
        this@SimpleServerInterceptor.onMessage(message, headers)
        super.onMessage(message)
    }

    override fun onHalfClose() {
        this@SimpleServerInterceptor.onHalfClose(headers)
        super.onHalfClose()
    }

    override fun onCancel() {
        this@SimpleServerInterceptor.onCancel(headers)
        super.onCancel()
    }

    override fun onComplete() {
        this@SimpleServerInterceptor.onComplete(headers)
        super.onComplete()
    }

    override fun onReady() {
        this@SimpleServerInterceptor.onReady(headers)
        super.onReady()
    }
}
    }
}