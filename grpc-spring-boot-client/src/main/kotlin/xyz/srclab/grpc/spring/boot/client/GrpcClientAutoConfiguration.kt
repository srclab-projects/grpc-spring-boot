package xyz.srclab.grpc.spring.boot.client

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class GrpcClientAutoConfiguration {

    @ConfigurationProperties(prefix = "grpc.client")
    @ConditionalOnMissingBean
    @Bean("xyz.srclab.spring.boot.grpc.client.GrpcClientsProperties")
    open fun grpcClientsProperties(): GrpcClientsProperties {
        return GrpcClientsProperties()
    }

    @ConditionalOnMissingBean
    @Bean("xyz.srclab.spring.boot.grpc.client.GrpcChannelFactory")
    open fun grpcChannelFactory(): GrpcChannelFactory {
        return DefaultGrpcChannelFactory()
    }

    @ConditionalOnMissingBean
    @Bean("xyz.srclab.spring.boot.grpc.client.GrpcClientBeanPostProcessor")
    open fun grpcClientBeanPostProcessor(): GrpcClientBeanPostProcessor {
        return GrpcClientBeanPostProcessor()
    }

    @ConditionalOnMissingBean
    @Bean("xyz.srclab.spring.boot.grpc.client.DefaultGrpcChannelConfigureHelper")
    open fun defaultGrpcChannelConfigureHelper(): DefaultGrpcChannelConfigureHelper {
        return DefaultGrpcChannelConfigureHelper()
    }
}