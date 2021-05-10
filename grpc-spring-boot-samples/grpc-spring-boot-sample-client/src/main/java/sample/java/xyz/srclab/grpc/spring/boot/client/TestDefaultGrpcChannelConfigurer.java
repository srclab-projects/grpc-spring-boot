package sample.java.xyz.srclab.grpc.spring.boot.client;

import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.netty.NettyChannelBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import xyz.srclab.grpc.spring.boot.client.DefaultGrpcChannelConfigurer;
import xyz.srclab.grpc.spring.boot.client.GrpcClientConfig;
import xyz.srclab.grpc.spring.boot.client.GrpcClientsConfig;

@Component
public class TestDefaultGrpcChannelConfigurer implements DefaultGrpcChannelConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(TestDefaultGrpcChannelConfigurer.class);

    @Override
    public void configureInProcessBuilder(
        @NotNull InProcessChannelBuilder builder,
        @NotNull GrpcClientsConfig clientsConfig,
        @NotNull GrpcClientConfig clientConfig
    ) {
        logger.info(">>>>configureInProcessBuilder");
    }

    @Override
    public void configureNettyBuilder(
        @NotNull NettyChannelBuilder builder,
        @NotNull GrpcClientsConfig clientsConfig,
        @NotNull GrpcClientConfig clientConfig
    ) {
        logger.info(">>>>configureNettyBuilder");
    }

    @Override
    public void configureShadedNettyBuilder(
        @NotNull io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder builder,
        @NotNull GrpcClientsConfig clientsConfig,
        @NotNull GrpcClientConfig clientConfig
    ) {
        logger.info(">>>>configureShadedNettyBuilder");
    }
}
