package xyz.srclab.grpc.spring.boot.context

import io.grpc.Context
import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.base.asAny
import xyz.srclab.grpc.spring.boot.context.GrpcContext.Companion.ARGS_KEY
import xyz.srclab.grpc.spring.boot.context.GrpcContext.Companion.CONTEXT_KEY

/**
 * A wrapper for [Context],
 * all arguments are encapsulated by key [ARGS_KEY] which associates a [MutableMap],
 * and key [CONTEXT_KEY] specifies the [GrpcContext] object itself.
 */
interface GrpcContext {

    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("rawContext")
    val rawContext: Context

    fun <V> get(key: Any): V

    @JvmDefault
    fun getString(key: Any): String? {
        return get<String?>(key)?.toString()
    }

    fun <V> set(key: Any, value: V)

    companion object {

        private val ARGS_KEY: Context.Key<MutableMap<Any, Any?>> =
                Context.key("MAP_KEY")

        private val CONTEXT_KEY: Context.Key<GrpcContext> =
                Context.key("CONTEXT_KEY")

        @JvmStatic
        fun current(): GrpcContext {
            return with(Context.current())
        }

        @JvmStatic
        fun with(context: Context): GrpcContext {
            val grpcContext: GrpcContext? = CONTEXT_KEY.get(context)
            if (grpcContext !== null) {
                return grpcContext
            }

            val newContext = GrpcContextImpl(context)
            val rawContext = context.withValues(CONTEXT_KEY, newContext, ARGS_KEY, HashMap())
            newContext.rawContext = rawContext
            return newContext
        }

        private class GrpcContextImpl(
                override var rawContext: Context
        ) : GrpcContext {

            override fun <V> get(key: Any): V {
                return ARGS_KEY.get(rawContext)[key].asAny()
            }

            override fun <V> set(key: Any, value: V) {
                ARGS_KEY.get(rawContext)[key] = value
            }
        }
    }
}