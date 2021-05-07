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
import xyz.srclab.common.collect.filter
import xyz.srclab.common.collect.map
import xyz.srclab.common.reflect.searchFields
import xyz.srclab.common.reflect.setValue
import xyz.srclab.grpc.spring.boot.client.interceptors.SimpleClientInterceptor
import java.lang.reflect.Field
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

    private val interceptorsBuilder: GrpcClientInterceptorsBuilder = GrpcClientInterceptorsBuilder.newBuilder()
    private val channels: MutableMap<String, Channel> = HashMap()

    @PostConstruct
    private fun init() {
        clientsConfig = grpcClientsProperties.toClientsConfig()
        clientConfigs = grpcClientsProperties.toClientConfigs()
        val interceptorInfos = applicationContext.getBeansOfType(ClientInterceptor::class.java)
            .map { key, value ->
                applicationContext.findAnnotationOnBean(key, GrpcClientInterceptor::class.java)
                key to GrpcClientInterceptorsBuilder.newInterceptorInfo(
                    value,
                    applicationContext.findAnnotationOnBean(key, GrpcClientInterceptor::class.java)
                )
            }
            .let {
                if (clientsConfig.needGrpcAnnotation) {
                    it.filter { e ->
                        e.value.annotation !== null
                    }
                } else {
                    it
                }
            }
            .plus(
                applicationContext.getBeansOfType(SimpleClientInterceptor::class.java)
                    .map { key, value ->
                        applicationContext.findAnnotationOnBean(key, GrpcClientInterceptor::class.java)
                        key to GrpcClientInterceptorsBuilder.newInterceptorInfo(
                            value,
                            applicationContext.findAnnotationOnBean(key, GrpcClientInterceptor::class.java)
                        )
                    }
            )
        interceptorsBuilder.addInterceptorInfos(interceptorInfos.values)
        for (interceptorEntry in interceptorInfos) {
            val beanName = interceptorEntry.key
            val info = interceptorEntry.value
            logger.debug("Load gRPC client interceptor: $beanName (${info.javaClass}).")
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
            return grpcChannelFactory.create(clientsConfig, clientConfig, interceptorsBuilder.buildFor(clientName))
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

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(GrpcClientBeanPostProcessor::class.java)
    }
}