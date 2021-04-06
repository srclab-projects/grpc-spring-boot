@file:JvmName("ProtobufJsons")

package xyz.srclab.spring.boot.grpc.protobuf

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.hubspot.jackson.datatype.protobuf.ProtobufModule
import xyz.srclab.common.serialize.json.JsonSerializer
import xyz.srclab.common.serialize.json.JsonSerializer.Companion.toJsonSerializer

internal val PROTOBUF_OBJECT_MAPPER by lazy {
    val mapper = JsonMapper()
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    val javaTimeModule = JavaTimeModule()
    mapper.registerModule(javaTimeModule)
    val protobufModule = ProtobufModule()
    mapper.registerModule(protobufModule)
    mapper
}

@JvmField
val PROTOBUF_JSON_SERIALIZER: JsonSerializer = PROTOBUF_OBJECT_MAPPER.toJsonSerializer()