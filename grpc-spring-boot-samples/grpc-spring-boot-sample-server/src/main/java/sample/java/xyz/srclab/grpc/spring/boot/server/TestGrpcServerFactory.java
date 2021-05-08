package sample.java.xyz.srclab.grpc.spring.boot.server;

import io.grpc.Server;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import xyz.srclab.grpc.spring.boot.server.DefaultGrpcServerFactory;
import xyz.srclab.grpc.spring.boot.server.GrpcServerConfig;
import xyz.srclab.grpc.spring.boot.server.GrpcServersConfig;
import xyz.srclab.grpc.spring.boot.server.GrpcServiceBuilder;

import java.util.Set;

@Component
public class TestGrpcServerFactory extends DefaultGrpcServerFactory {

    private static final Logger logger = LoggerFactory.getLogger(TestGrpcServerFactory.class);

    @NotNull
    @Override
    public Server create(
        @NotNull GrpcServersConfig serversConfig,
        @NotNull GrpcServerConfig serverDefinition,
        @NotNull Set<? extends GrpcServiceBuilder> serviceBuilders
    ) {
        logger.info(">>>>TestGrpcServerFactory.create");
        return super.create(serversConfig, serverDefinition, serviceBuilders);
    }
}
