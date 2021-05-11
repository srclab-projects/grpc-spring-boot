package xyz.srclab.grpc.spring.boot.client

import io.grpc.Attributes
import io.grpc.EquivalentAddressGroup
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

        override val addresses: List<EquivalentAddressGroup>

        init {
            fun String.resolveEquivalentAddressGroup(): EquivalentAddressGroup {
                val socketAddresses: MutableList<SocketAddress> = LinkedList()
                val authorityAndHostIp = this.trim().split("/")

                fun resolveAuthorityAndHostIp(authority: String?, hostAndPort: List<String>): EquivalentAddressGroup {
                    if (hostAndPort.size != 2) {
                        val hostAddresses = InetAddress.getAllByName(hostAndPort[0].trim())
                        for (hostAddress in hostAddresses) {
                            socketAddresses.add(InetSocketAddress(hostAddress.hostAddress, DEFAULT_PORT))
                        }
                    } else {
                        val hostAddresses = InetAddress.getAllByName(hostAndPort[0].trim())
                        val port = hostAndPort[1].trim().toInt()
                        for (hostAddress in hostAddresses) {
                            socketAddresses.add(InetSocketAddress(hostAddress.hostAddress, port))
                        }
                    }
                    return if (authority === null) {
                        EquivalentAddressGroup(socketAddresses)
                    } else {
                        EquivalentAddressGroup(
                            socketAddresses,
                            Attributes.newBuilder()
                                .set(EquivalentAddressGroup.ATTR_AUTHORITY_OVERRIDE, authority)
                                .build()
                        )
                    }
                }

                return if (authorityAndHostIp.size != 2) {
                    val hostAndPort = this.trim().split(":")
                    resolveAuthorityAndHostIp(null, hostAndPort)
                } else {
                    val authority = authorityAndHostIp[0].trim()
                    val hostAndPort = authorityAndHostIp[1].trim().split(":")
                    resolveAuthorityAndHostIp(authority, hostAndPort)
                }
            }

            val addressStrings = targetUri.schemeSpecificPart.split(",")
            val equivalentAddressGroups: MutableList<EquivalentAddressGroup> = LinkedList()
            for (addressString in addressStrings) {
                equivalentAddressGroups.add(addressString.resolveEquivalentAddressGroup())
            }
            addresses = equivalentAddressGroups.toList()
        }
    }
}