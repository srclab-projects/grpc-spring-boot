package sample.java.xyz.srclab.grpc.spring.boot.server;

import io.grpc.netty.NettyServerBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import xyz.srclab.grpc.spring.boot.server.DefaultGrpcServerConfigurer;
import xyz.srclab.grpc.spring.boot.server.GrpcServerConfig;
import xyz.srclab.grpc.spring.boot.server.GrpcServersConfig;

@Component
public class TestDefaultGrpcServerConfigurer implements DefaultGrpcServerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(TestDefaultGrpcServerConfigurer.class);

    @Override
    public void configureNettyBuilder(
        @NotNull NettyServerBuilder builder,
        @NotNull GrpcServersConfig serversConfig,
        @NotNull GrpcServerConfig serverConfig
    ) {
        logger.info(">>>>configureNettyBuilder");
    }

    @Override
    public void configureShadedNettyBuilder(
        @NotNull io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder builder,
        @NotNull GrpcServersConfig serversConfig,
        @NotNull GrpcServerConfig serverConfig
    ) {
        logger.info(">>>>configureShadedNettyBuilder");
    }
}
