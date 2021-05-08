package sample.java.xyz.srclab.grpc.spring.boot.client;

import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import xyz.srclab.grpc.spring.boot.client.DefaultGrpcChannelFactory;
import xyz.srclab.grpc.spring.boot.client.GrpcClientConfig;
import xyz.srclab.grpc.spring.boot.client.GrpcClientsConfig;

import java.util.List;

@Component
public class TestGrpcChannelFactory extends DefaultGrpcChannelFactory {

    private static final Logger logger = LoggerFactory.getLogger(TestGrpcChannelFactory.class);

    @NotNull
    @Override
    public Channel create(
        @NotNull GrpcClientsConfig grpcClientsConfig,
        @NotNull GrpcClientConfig grpcClientConfig,
        @NotNull List<? extends ClientInterceptor> interceptors
    ) {
        logger.info(">>>>TestGrpcChannelFactory.create");
        return super.create(grpcClientsConfig, grpcClientConfig, interceptors);
    }
}
