@file:JvmName("ProtobufConverts")

package xyz.srclab.spring.boot.protobuf

import xyz.srclab.common.convert.BeanConvertHandler
import xyz.srclab.common.convert.ConvertHandler
import xyz.srclab.common.convert.Converter

@JvmField
val PROTOBUF_CONVERTER: Converter = Converter.newConverter(
    ConvertHandler.defaultsWithBeanConvertHandler(
        BeanConvertHandler(PROTOBUF_BEAN_RESOLVER)
    )
)