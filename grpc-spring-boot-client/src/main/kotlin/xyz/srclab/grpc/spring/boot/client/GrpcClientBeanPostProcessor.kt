package xyz.srclab.grpc.spring.boot.client

import io.grpc.Channel
import io.grpc.ClientInterceptor
import io.grpc.stub.AbstractStub
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.ApplicationContext
import xyz.srclab.common.collect.filter
import xyz.srclab.common.collect.map
import xyz.srclab.common.reflect.rawClass
import xyz.srclab.common.reflect.searchFields
import xyz.srclab.common.reflect.setValue
import xyz.srclab.grpc.spring.boot.client.interceptors.SimpleClientInterceptor
import java.lang.reflect.ParameterizedType
import javax.annotation.PostConstruct
import javax.annotation.Resource

open class GrpcClientBeanPostProcessor : BeanPostProcessor {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Resource
    private lateinit var grpcClientsProperties: GrpcClientsProperties

    @Resource
    private lateinit var grpcChannelFactory: GrpcChannelFactory

    @Resource
    private lateinit var grpcStubFactory: GrpcStubFactory

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

        fun findClientConfig(clientName: String): GrpcClientConfig? {
            if (clientName.isEmpty() && clientConfigs.isNotEmpty()) {
                return clientConfigs.values.first()
            }
            return clientConfigs[clientName]
        }

        fun newOrExistedChannel(clientConfig: GrpcClientConfig): Channel? {
            val existedChannel = channels[clientConfig.name]
            if (existedChannel !== null) {
                return existedChannel
            }
            return grpcChannelFactory.create(
                clientsConfig, clientConfig, interceptorsBuilder.buildFor(clientConfig.name))
        }

        val fields = bean.javaClass.searchFields(true) { true }
        for (field in fields) {
            val grpcClient = field.getAnnotation(GrpcClient::class.java)
            if (grpcClient === null) {
                continue
            }
            val clientName = grpcClient.valueOrClientName
            val clientConfig = findClientConfig(clientName)
            if (clientConfig === null) {
                throw IllegalArgumentException(
                    "gRPC client on ${bean.javaClass.name}.${field.name} not found: $clientName"
                )
            }
            val channel = newOrExistedChannel(clientConfig)
            if (channel === null) {
                throw IllegalArgumentException(
                    "gRPC client on ${bean.javaClass.name}.${field.name} not found: $clientName"
                )
            }
            channels[clientName] = channel

            val fieldType = field.genericType
            when {
                fieldType is Class<*> && Channel::class.java.isAssignableFrom(fieldType) -> {
                    logger.debug("Set gRPC client channel on ${bean.javaClass.name}.${field.name}")
                    field.setValue(bean, channel, true)
                }
                fieldType is Class<*> && AbstractStub::class.java.isAssignableFrom(fieldType) -> {
                    val newStub = fieldType.newStub<AbstractStub<*>>(channel)
                    logger.debug("Set gRPC client stub on ${this.javaClass.name}.${field.name}")
                    field.setValue(bean, newStub, true)
                }
                fieldType is ParameterizedType && GrpcStub::class.java.isAssignableFrom(fieldType.rawClass) -> {
                    val actualArgs = fieldType.actualTypeArguments
                    if (actualArgs.isNullOrEmpty() || actualArgs.size != 1) {
                        throw IllegalStateException("Not a valid GrpcStub type: $fieldType")
                    }
                    val grpcStub = GrpcStubHelper(
                        grpcStubFactory, clientsConfig, clientConfig, actualArgs[0].rawClass, channel)
                    logger.debug("Set gRPC client GrpcStub on ${this.javaClass.name}.${field.name}")
                    field.setValue(bean, grpcStub, true)
                }
            }
        }

        return bean
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(GrpcClientBeanPostProcessor::class.java)
    }
}