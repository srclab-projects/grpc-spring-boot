package xyz.srclab.grpc.spring.boot.server.interceptors

import io.grpc.*
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall

/**
 * Provides a skeletal implementation of [ServerInterceptor]
 */
interface SimpleServerInterceptor : ServerInterceptor {

    @JvmDefault
    fun <ReqT : Any, RespT : Any> intercept(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ) {
    }

    @JvmDefault
    fun <ReqT : Any> onMessage(message: ReqT) {
    }

    @JvmDefault
    fun onHalfClose() {
    }

    @JvmDefault
    fun onCancel() {
    }

    @JvmDefault
    fun onComplete() {
    }

    @JvmDefault
    fun onReady() {
    }

    @JvmDefault
    fun <RespT : Any> sendMessage(message: RespT) {
    }

    @JvmDefault
    fun sendHeaders(headers: Metadata) {
    }

    @JvmDefault
    fun close(status: Status, trailers: Metadata) {
    }

    @JvmDefault
    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {

        intercept(call, headers, next)

        val delegate = next.startCall(
            object : SimpleForwardingServerCall<ReqT, RespT>(call) {

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
            },
            headers
        )

        return object : ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(delegate) {

            override fun onMessage(message: ReqT) {
                this@SimpleServerInterceptor.onMessage(message)
                super.onMessage(message)
            }

            override fun onHalfClose() {
                this@SimpleServerInterceptor.onHalfClose()
                super.onHalfClose()
            }

            override fun onCancel() {
                this@SimpleServerInterceptor.onCancel()
                super.onCancel()
            }

            override fun onComplete() {
                this@SimpleServerInterceptor.onComplete()
                super.onComplete()
            }

            override fun onReady() {
                this@SimpleServerInterceptor.onReady()
                super.onReady()
            }
        }
    }
}