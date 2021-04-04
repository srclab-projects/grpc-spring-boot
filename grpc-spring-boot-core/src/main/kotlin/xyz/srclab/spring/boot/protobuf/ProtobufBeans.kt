@file:JvmName("ProtobufBeans")

package xyz.srclab.spring.boot.protobuf

import xyz.srclab.common.bean.BeanCopyOptions
import xyz.srclab.common.bean.BeanResolver

@JvmField
val PROTOBUF_BEAN_RESOLVER: BeanResolver = BeanResolver.DEFAULT.withPreResolveHandler(ProtobufBeanResolveHandler)

@JvmField
val PROTOBUF_BEAN_COPY_OPTIONS: BeanCopyOptions = BeanCopyOptions.DEFAULT
    .withBeanResolver(PROTOBUF_BEAN_RESOLVER)
    .withConverter(PROTOBUF_CONVERTER)