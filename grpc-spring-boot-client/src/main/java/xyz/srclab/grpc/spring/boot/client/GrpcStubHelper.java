package xyz.srclab.grpc.spring.boot.client;

import io.grpc.Channel;
import io.grpc.stub.AbstractStub;
import org.jetbrains.annotations.NotNull;

class GrpcStubHelper implements GrpcStub {

    private final GrpcStubFactory grpcStubFactory;
    private final GrpcClientsConfig clientsConfig;
    private final GrpcClientConfig clientConfig;
    private final Class stubClass;
    private final Channel channel;

    GrpcStubHelper(
        GrpcStubFactory grpcStubFactory,
        GrpcClientsConfig clientsConfig,
        GrpcClientConfig clientConfig,
        Class stubClass,
        Channel channel
    ) {
        this.grpcStubFactory = grpcStubFactory;
        this.clientsConfig = clientsConfig;
        this.clientConfig = clientConfig;
        this.stubClass = stubClass;
        this.channel = channel;
    }

    @NotNull
    @Override
    public AbstractStub get() {
        return grpcStubFactory.create(clientsConfig, clientConfig, stubClass, channel);
    }
}
