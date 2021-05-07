package xyz.srclab.grpc.spring.boot.client

import io.grpc.Channel
import io.grpc.stub.AbstractStub

/**
 * Factory to create gRPC server.
 */
interface GrpcStubFactory {

    fun create(
        clientsConfig: GrpcClientsConfig,
        clientConfig: GrpcClientConfig,
        stubClass: Class<*>,
        channel: Channel
    ): AbstractStub<*>
}