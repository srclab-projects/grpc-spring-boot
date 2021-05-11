package xyz.srclab.grpc.spring.boot.client

import io.grpc.Attributes
import io.grpc.EquivalentAddressGroup.ATTR_AUTHORITY_OVERRIDE
import io.grpc.NameResolver
import io.grpc.NameResolverProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI

/**
 * Simple load balance target resolver provider:
 *
 * ```
 * lb:authority1/host1:port1,authority2/host2:port2...
 * ```
 */
internal class LbNameResolverProvider(
    private val targetResolver: GrpcTargetResolver
) : NameResolverProvider() {

    private val logger: Logger = LoggerFactory.getLogger(LbNameResolverProvider::class.java)

    override fun getDefaultScheme(): String = "lb"

    override fun isAvailable(): Boolean = true

    override fun priority(): Int = 5

    override fun newNameResolver(targetUri: URI, args: NameResolver.Args): NameResolver {
        logger.info("Create LbNameResolver for uri: $targetUri, args: $args")
        return LbNameResolver(targetResolver, targetUri, args)
    }

    private class LbNameResolver(
        targetResolver: GrpcTargetResolver,
        targetUri: URI,
        args: NameResolver.Args
    ) : NameResolver() {

        private val target: GrpcTarget = targetResolver.resolve(targetUri, args)

        override fun getServiceAuthority(): String = ATTR_AUTHORITY_OVERRIDE.toString()

        override fun shutdown() {
        }

        override fun start(listener: Listener) {
            listener.onAddresses(
                target.addresses,
                Attributes.EMPTY
            )
        }
    }
}