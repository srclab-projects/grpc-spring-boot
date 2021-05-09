package xyz.srclab.grpc.spring.boot.client

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import java.net.SocketAddress

interface GrpcTarget {

    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("authority")
    val authority: String

    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("addresses")
    val addresses: List<SocketAddress>
}