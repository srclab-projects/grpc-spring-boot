package xyz.srclab.grpc.spring.boot.client

import io.grpc.*
import org.springframework.util.AntPathMatcher
import xyz.srclab.common.collect.sorted
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.grpc.spring.boot.client.interceptors.AbstractClientInterceptor
import xyz.srclab.grpc.spring.boot.client.interceptors.SimpleClientInterceptor
import xyz.srclab.grpc.spring.boot.context.GrpcContext
import java.util.*

interface GrpcClientInterceptorsBuilder {

    fun addInterceptorInfos(interceptorInfos: Iterable<InterceptorInfo>)

    fun buildFor(clientName: String): List<ClientInterceptor>

    interface InterceptorInfo {

        @Suppress(INAPPLICABLE_JVM_NAME)
        @get:JvmName("interceptor")
        val interceptor: Any

        @Suppress(INAPPLICABLE_JVM_NAME)
        @get:JvmName("annotation")
        val annotation: GrpcClientInterceptor?

        @Suppress(INAPPLICABLE_JVM_NAME)
        @get:JvmName("clientPatterns")
        val clientPatterns: List<String>

        @Suppress(INAPPLICABLE_JVM_NAME)
        @get:JvmName("order")
        val order: Int
    }

    companion object {

        @JvmStatic
        fun newInterceptorInfo(
            interceptor: Any,
            annotation: GrpcClientInterceptor?
        ): InterceptorInfo {
            return object : InterceptorInfo {
                override val interceptor: Any = interceptor
                override val annotation: GrpcClientInterceptor? = annotation
                override val clientPatterns: List<String> = annotation?.valueOrClientPatterns ?: emptyList()
                override val order: Int = annotation?.order ?: 0
            }
        }

        @JvmStatic
        fun newBuilder(): GrpcClientInterceptorsBuilder {
            return object : GrpcClientInterceptorsBuilder {

                private val antPathMatcher = AntPathMatcher()
                private val interceptorInfos: MutableList<InterceptorInfo> = LinkedList()

                override fun addInterceptorInfos(interceptorInfos: Iterable<InterceptorInfo>) {
                    this.interceptorInfos.addAll(interceptorInfos)
                }

                override fun buildFor(clientName: String): List<ClientInterceptor> {
                    val interceptorInfos = interceptorInfos.filter {
                        if (it.clientPatterns.isEmpty()) {
                            true
                        } else {
                            for (clientPattern in it.clientPatterns) {
                                if (antPathMatcher.match(clientPattern, clientName)) {
                                    return@filter true
                                }
                            }
                            false
                        }
                    }

                    //Group by interceptor type (ClientInterceptor or SimpleClientInterceptor)
                    val groups = interceptorInfos.groupBy {
                        if (it.interceptor is ClientInterceptor)
                            0
                        else if (it.interceptor is SimpleClientInterceptor)
                            1
                        else
                            throw IllegalStateException("Unknown client interceptor: ${it.interceptor}")
                    }
                    val simpleInterceptor = SimpleServerInterceptorImpl(
                        (groups[1] ?: emptyList())
                            .sorted { e1, e2 ->
                                e1.order - e2.order
                            }
                            .map {
                                it.interceptor as SimpleClientInterceptor
                            }
                    )
                    return (groups[0] ?: emptyList())
                        .plus(newInterceptorInfo(simpleInterceptor, null))
                        .sorted { e1, e2 ->
                            //Note: gRPC interceptors follow the FILO,
                            //means first added interceptor will be called last:
                            //Add order   : interceptor1, interceptor2, interceptor3
                            //Called order: interceptor3, interceptor2, interceptor1
                            e2.order - e1.order
                        }
                        .map {
                            it.interceptor as ClientInterceptor
                        }
                }
            }
        }

        private class SimpleServerInterceptorImpl(
            private val interceptors: List<SimpleClientInterceptor>
        ) : AbstractClientInterceptor() {

            override fun <ReqT : Any, RespT : Any> intercept(
                method: MethodDescriptor<ReqT, RespT>, callOptions: CallOptions, context: GrpcContext
            ) {
                for (interceptor in interceptors) {
                    interceptor.intercept(method, callOptions, context)
                }
            }

            override fun <ReqT : Any, RespT : Any> sendHeaders(
                sentHeader: Metadata,
                method: MethodDescriptor<ReqT, RespT>,
                callOptions: CallOptions,
                context: GrpcContext
            ) {
                for (interceptor in interceptors) {
                    interceptor.sendHeaders(sentHeader, method, callOptions, context)
                }
            }

            override fun <ReqT : Any, RespT : Any> sendMessage(
                sentMessage: ReqT,
                method: MethodDescriptor<ReqT, RespT>,
                callOptions: CallOptions,
                context: GrpcContext
            ) {
                for (interceptor in interceptors) {
                    interceptor.sendMessage(sentMessage, method, callOptions, context)
                }
            }

            override fun <ReqT : Any, RespT : Any> onReady(
                method: MethodDescriptor<ReqT, RespT>, callOptions: CallOptions, context: GrpcContext
            ) {
                for (interceptor in interceptors) {
                    interceptor.onReady(method, callOptions, context)
                }
            }

            override fun <ReqT : Any, RespT : Any> onHeaders(
                headers: Metadata,
                method: MethodDescriptor<ReqT, RespT>,
                callOptions: CallOptions,
                context: GrpcContext
            ) {
                for (interceptor in interceptors) {
                    interceptor.onHeaders(headers, method, callOptions, context)
                }
            }

            override fun <ReqT : Any, RespT : Any> onMessage(
                message: RespT,
                method: MethodDescriptor<ReqT, RespT>,
                callOptions: CallOptions,
                context: GrpcContext
            ) {
                for (interceptor in interceptors) {
                    interceptor.onMessage(message, method, callOptions, context)
                }
            }

            override fun <ReqT : Any, RespT : Any> onClose(
                status: Status,
                trailers: Metadata,
                method: MethodDescriptor<ReqT, RespT>,
                callOptions: CallOptions,
                context: GrpcContext
            ) {
                for (interceptor in interceptors) {
                    interceptor.onClose(status, trailers, method, callOptions, context)
                }
            }
        }
    }
}