package xyz.srclab.grpc.spring.boot.client

import io.grpc.EquivalentAddressGroup
import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME

interface GrpcTarget {

    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("addresses")
    val addresses: List<EquivalentAddressGroup>
}