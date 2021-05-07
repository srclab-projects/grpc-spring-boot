package xyz.srclab.grpc.spring.boot.server

import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerInterceptor
import io.grpc.Status
import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.collect.sorted
import xyz.srclab.grpc.spring.boot.context.GrpcContext
import xyz.srclab.grpc.spring.boot.server.interceptors.AbstractServerInterceptor
import xyz.srclab.grpc.spring.boot.server.interceptors.SimpleServerInterceptor
import java.util.*

interface GrpcServerInterceptorsBuilder {

    fun addInterceptorInfo(interceptorInfo: InterceptorInfo)

    fun build(): List<ServerInterceptor>

    interface InterceptorInfo {

        @Suppress(INAPPLICABLE_JVM_NAME)
        @get:JvmName("interceptor")
        val interceptor: Any

        @Suppress(INAPPLICABLE_JVM_NAME)
        @get:JvmName("annotation")
        val annotation: GrpcServerInterceptor?

        @Suppress(INAPPLICABLE_JVM_NAME)
        @get:JvmName("servicePatterns")
        val servicePatterns: List<String>

        @Suppress(INAPPLICABLE_JVM_NAME)
        @get:JvmName("order")
        val order: Int
    }

    companion object {

        @JvmStatic
        fun newInterceptorInfo(
            interceptor: Any,
            annotation: GrpcServerInterceptor?,
            servicePatterns: List<String>,
            order: Int
        ): InterceptorInfo {
            return object : InterceptorInfo {
                override val interceptor: Any = interceptor
                override val annotation: GrpcServerInterceptor? = annotation
                override val servicePatterns: List<String> = servicePatterns
                override val order: Int = order
            }
        }

        @JvmStatic
        fun newBuilder(): GrpcServerInterceptorsBuilder {
            return object : GrpcServerInterceptorsBuilder {

                private val interceptorInfos: MutableList<InterceptorInfo> = LinkedList()

                override fun addInterceptorInfo(interceptorInfo: InterceptorInfo) {
                    interceptorInfos.add(interceptorInfo)
                }

                override fun build(): List<ServerInterceptor> {
                    //Group by interceptor type (ServerInterceptor or SimpleServerInterceptor)
                    val groups = interceptorInfos.groupBy {
                        if (it.interceptor is ServerInterceptor)
                            0
                        else
                            1
                    }
                    val simpleInterceptor = SimpleServerInterceptorImpl(
                        (groups[1] ?: emptyList())
                            .sorted { e1, e2 ->
                                e1.order - e2.order
                            }
                            .map {
                                it.interceptor as SimpleServerInterceptor
                            }
                    )
                    return (groups[0] ?: emptyList())
                        .plus(newInterceptorInfo(simpleInterceptor, null, emptyList(), 0))
                        .sorted { e1, e2 ->
                            //Note: gRPC interceptors follow the FILO,
                            //means first added interceptor will be called last:
                            //Add order   : interceptor1, interceptor2, interceptor3
                            //Called order: interceptor3, interceptor2, interceptor1
                            e2.order - e1.order
                        }
                        .map {
                            it.interceptor as ServerInterceptor
                        }
                }
            }
        }

        private class SimpleServerInterceptorImpl(
            private val interceptors: List<SimpleServerInterceptor>
        ) : AbstractServerInterceptor() {

            override fun <ReqT : Any, RespT : Any> intercept(
                call: ServerCall<ReqT, RespT>, headers: Metadata, context: GrpcContext) {
                for (interceptor in interceptors) {
                    interceptor.intercept(call, headers, context)
                }
            }

            override fun <ReqT : Any, RespT : Any> onReady(
                call: ServerCall<ReqT, RespT>, headers: Metadata, context: GrpcContext) {
                for (interceptor in interceptors) {
                    interceptor.onReady(call, headers, context)
                }
            }

            override fun <ReqT : Any, RespT : Any> onMessage(
                message: ReqT, call: ServerCall<ReqT, RespT>, headers: Metadata, context: GrpcContext) {
                for (interceptor in interceptors) {
                    interceptor.onMessage(message, call, headers, context)
                }
            }

            override fun <ReqT : Any, RespT : Any> onHalfClose(
                call: ServerCall<ReqT, RespT>, headers: Metadata, context: GrpcContext) {
                for (interceptor in interceptors) {
                    interceptor.onHalfClose(call, headers, context)
                }
            }

            override fun <ReqT : Any, RespT : Any> sendHeaders(
                sentHeader: Metadata, call: ServerCall<ReqT, RespT>, headers: Metadata, context: GrpcContext) {
                for (interceptor in interceptors) {
                    interceptor.sendHeaders(sentHeader, call, headers, context)
                }
            }

            override fun <ReqT : Any, RespT : Any> sendMessage(
                sentMessage: RespT, call: ServerCall<ReqT, RespT>, headers: Metadata, context: GrpcContext) {
                for (interceptor in interceptors) {
                    interceptor.sendMessage(sentMessage, call, headers, context)
                }
            }

            override fun <ReqT : Any, RespT : Any> close(
                status: Status,
                trailers: Metadata,
                call: ServerCall<ReqT, RespT>,
                headers: Metadata,
                context: GrpcContext
            ) {
                for (interceptor in interceptors) {
                    interceptor.close(status, trailers, call, headers, context)
                }
            }

            override fun <ReqT : Any, RespT : Any> onCancel(
                call: ServerCall<ReqT, RespT>, headers: Metadata, context: GrpcContext) {
                for (interceptor in interceptors) {
                    interceptor.onCancel(call, headers, context)
                }
            }

            override fun <ReqT : Any, RespT : Any> onComplete(
                call: ServerCall<ReqT, RespT>, headers: Metadata, context: GrpcContext) {
                for (interceptor in interceptors) {
                    interceptor.onComplete(call, headers, context)
                }
            }

            override fun <ReqT : Any, RespT : Any> onException(
                cause: Throwable, call: ServerCall<ReqT, RespT>, headers: Metadata, context: GrpcContext) {
                for (interceptor in interceptors) {
                    interceptor.onException(cause, call, headers, context)
                }
            }
        }
    }
}