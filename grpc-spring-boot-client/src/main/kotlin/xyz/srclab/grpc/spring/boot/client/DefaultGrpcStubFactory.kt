package xyz.srclab.grpc.spring.boot.client

import io.grpc.Channel
import io.grpc.stub.AbstractStub
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class DefaultGrpcStubFactory : GrpcStubFactory {

    override fun create(
        clientsConfig: GrpcClientsConfig,
        clientConfig: GrpcClientConfig,
        stubClass: Class<*>,
        channel: Channel
    ): AbstractStub<*> {
        val stub: AbstractStub<*> = stubClass.newStub(channel, clientConfig)
        logger.info("gRPC stub created: ${clientConfig.name}.${stubClass.name}")
        return stub
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(DefaultGrpcStubFactory::class.java)
    }
}