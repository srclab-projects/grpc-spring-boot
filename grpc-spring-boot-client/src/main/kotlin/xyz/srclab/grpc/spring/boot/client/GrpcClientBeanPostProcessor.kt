package xyz.srclab.grpc.spring.boot.client

import io.grpc.Channel
import io.grpc.ClientInterceptor
import io.grpc.stub.AbstractAsyncStub
import io.grpc.stub.AbstractBlockingStub
import io.grpc.stub.AbstractFutureStub
import io.grpc.stub.AbstractStub
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.util.AntPathMatcher
import xyz.srclab.common.collect.filter
import xyz.srclab.common.collect.sorted
import xyz.srclab.common.reflect.searchFields
import xyz.srclab.common.reflect.setValue
import java.lang.reflect.Field
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.Resource

open class GrpcClientBeanPostProcessor : BeanPostProcessor {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Resource
    private lateinit var grpcClientsProperties: GrpcClientsProperties

    @Resource
    private lateinit var grpcChannelFactory: GrpcChannelFactory

    private lateinit var clientsConfig: GrpcClientsConfig
    private lateinit var clientConfigs: Map<String, GrpcClientConfig>
    private lateinit var interceptors: List<ClientInterceptorInfo>

    private val channels: MutableMap<String, Channel> = HashMap()
    private val antPathMatcher = AntPathMatcher()

    @PostConstruct
    private fun init() {
        clientsConfig = grpcClientsProperties.toClientsConfig()
        clientConfigs = grpcClientsProperties.toClientConfigs()
        interceptors = applicationContext.getBeansOfType(ClientInterceptor::class.java)
            .map {
                ClientInterceptorInfo(
                    it.value,
                    applicationContext.findAnnotationOnBean(it.key, GrpcClientInterceptor::class.java)
                )
            }
            .sorted { e1, e2 ->
                fun ClientInterceptorInfo.order(): Int {
                    return if (this.annotation === null) 0 else this.annotation.order
                }
                //Note: gRPC interceptors follow the FILO, means first added interceptor will be called last:
                //Add order   : interceptor1, interceptor2, interceptor3
                //Called order: interceptor3, interceptor2, interceptor1
                e2.order() - e1.order()
            }
            .let {
                if (clientsConfig.needGrpcAnnotation) {
                    it.filter { e ->
                        e.annotation !== null
                    }
                } else {
                    it
                }
            }
    }

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {

        fun newOrExistedChannel(clientName: String): Channel? {
            val clientConfig = clientConfigs[clientName]
            if (clientConfig === null) {
                return null
            }
            val existedChannel = channels[clientName]
            if (existedChannel !== null) {
                return existedChannel
            }
            val matchedInterceptors: MutableList<ClientInterceptor> = LinkedList()
            for (interceptor in interceptors) {
                val annotation = interceptor.annotation
                if (annotation === null || annotation.valueOrClientPatterns.isEmpty()) {
                    matchedInterceptors.add(interceptor.interceptor)
                } else {
                    val clientPatterns = annotation.valueOrClientPatterns
                    for (clientPattern in clientPatterns) {
                        if (antPathMatcher.match(clientPattern, clientName)) {
                            matchedInterceptors.add(interceptor.interceptor)
                        }
                    }
                }
            }
            return grpcChannelFactory.create(clientsConfig, clientConfig, matchedInterceptors)
        }

        fun <S : AbstractStub<S>> Field.generateStub(channel: Channel): S? {
            val stubClass = this.type
            val grpcClass = stubClass.declaringClass
            return when {
                AbstractAsyncStub::class.java.isAssignableFrom(stubClass) -> grpcClass.newStub<S>(channel)
                AbstractBlockingStub::class.java.isAssignableFrom(stubClass) -> grpcClass.newBlockingStub<S>(channel)
                AbstractFutureStub::class.java.isAssignableFrom(stubClass) -> grpcClass.newFutureStub<S>(channel)
                else -> null
            }
        }

        val fields = bean.javaClass.searchFields(true) { true }
        for (field in fields) {
            val grpcClient = field.getAnnotation(GrpcClient::class.java)
            if (grpcClient === null) {
                continue
            }
            val clientName = grpcClient.clientNameOrDefaultName(clientConfigs)
            val channel = newOrExistedChannel(clientName)
            if (channel === null) {
                throw IllegalArgumentException(
                    "gRPC client defined on ${bean.javaClass.name}.${field.name} was not found: $clientName"
                )
            }
            channels[clientName] = channel

            if (Channel::class.java.isAssignableFrom(field.type)) {
                logger.debug("Set gRPC client channel on ${bean.javaClass.name}.${field.name}")
                field.setValue(bean, channel, true)
                continue
            }
            val newStub = field.generateStub<AbstractStub<*>>(channel)
            if (newStub === null) {
                throw IllegalArgumentException(
                    "GrpcClient annotation should be on the Channel or Stub type: ${this.javaClass.name}.${field.name}"
                )
            }
            logger.debug("Set gRPC client stub on ${this.javaClass.name}.${field.name}")
            field.setValue(bean, newStub, true)
        }

        return bean
    }

    private data class ClientInterceptorInfo(
        val interceptor: ClientInterceptor,
        val annotation: GrpcClientInterceptor?,
    )

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(GrpcClientBeanPostProcessor::class.java)
    }
}