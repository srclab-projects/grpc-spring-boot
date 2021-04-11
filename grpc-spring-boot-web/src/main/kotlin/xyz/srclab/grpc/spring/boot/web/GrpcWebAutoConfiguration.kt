package xyz.srclab.grpc.spring.boot.web

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.hubspot.jackson.datatype.protobuf.ProtobufModule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.srclab.common.protobuf.PROTOBUF_OBJECT_MAPPER

@Configuration
open class GrpcWebAutoConfiguration {

    @Bean
    open fun jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { builder ->
            builder.configure(PROTOBUF_OBJECT_MAPPER)
            builder.propertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
            builder.modulesToInstall(ProtobufModule::class.java)
        }
    }
}