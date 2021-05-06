package xyz.srclab.grpc.spring.boot.server.interceptors

import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor

/**
 * Abstract [ServerInterceptor] to do with metadata (headers).
 */
abstract class MetadataServerInterceptor : ServerInterceptor {

    /**
     * Do with metadata (headers).
     */
    abstract fun <ReqT : Any, RespT : Any> doMetadata(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
    )

    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        doMetadata(call, headers)
        return next.startCall(call, headers)
    }
}