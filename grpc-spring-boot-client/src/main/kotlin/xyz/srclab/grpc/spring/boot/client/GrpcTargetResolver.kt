package xyz.srclab.grpc.spring.boot.client

import io.grpc.NameResolver
import java.net.URI

/**
 * Factory to resolve gRPC target.
 */
interface GrpcTargetResolver {

    fun resolve(targetUri: URI, args: NameResolver.Args): GrpcTarget
}