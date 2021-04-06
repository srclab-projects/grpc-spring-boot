package xyz.srclab.spring.boot.grpc.autoconfigure

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter

@Configuration
open class GrpcAutoConfiguration {

    @ConditionalOnWebApplication
    @ConditionalOnMissingBean
    @Bean
    open fun protobufHttpMessageConverter(): ProtobufHttpMessageConverter {
        return ProtobufHttpMessageConverter()
    }
}