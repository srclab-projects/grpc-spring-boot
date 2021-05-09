package xyz.srclab.grpc.spring.boot.client

import io.grpc.NameResolver
import xyz.srclab.grpc.spring.boot.DEFAULT_PORT
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.net.URI
import java.util.*

open class DefaultGrpcTargetResolver : GrpcTargetResolver {

    override fun resolve(targetUri: URI, args: NameResolver.Args): GrpcTarget {
        return GrpcTargetImpl(targetUri, args)
    }

    private inner class GrpcTargetImpl(
        targetUri: URI,
        args: NameResolver.Args
    ) : GrpcTarget {

        override val authority: String
        override val addresses: List<SocketAddress>

        init {
            val authorityAndAddresses = targetUri.schemeSpecificPart.split("/")

            fun String.resolveAddresses(): List<SocketAddress> {
                val inetAddresses: MutableList<SocketAddress> = LinkedList()
                val parts = this.split(",")
                for (part in parts) {
                    val hostAndPort = part.split(":")
                    if (hostAndPort.size != 2) {
                        val actualAddresses = InetAddress.getAllByName(hostAndPort[0].trim())
                        for (actualAddress in actualAddresses) {
                            inetAddresses.add(InetSocketAddress(actualAddress.hostAddress, DEFAULT_PORT))
                        }
                    } else {
                        val actualAddresses = InetAddress.getAllByName(hostAndPort[0].trim())
                        for (actualAddress in actualAddresses) {
                            inetAddresses.add(InetSocketAddress(actualAddress.hostAddress, hostAndPort[1].toInt()))
                        }
                    }
                }
                return inetAddresses
            }

            if (authorityAndAddresses.size != 2) {
                authority = ""
                addresses = targetUri.schemeSpecificPart.trim().resolveAddresses()
            } else {
                authority = authorityAndAddresses[0].trim()
                addresses = authorityAndAddresses[1].trim().resolveAddresses()
            }
        }
    }
}