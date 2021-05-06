package test.xyz.srclab.grpc.spring.boot.client;

import io.grpc.netty.NettyChannelBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.test.TestMarker;
import xyz.srclab.grpc.spring.boot.client.DefaultGrpcChannelConfigurer;
import xyz.srclab.grpc.spring.boot.client.GrpcClientConfig;
import xyz.srclab.grpc.spring.boot.client.GrpcClientsConfig;

import java.util.LinkedList;
import java.util.List;

@Component
public class TestDefaultGrpcChannelConfigurer implements DefaultGrpcChannelConfigurer {

    private final TestMarker testMarker = TestMarker.newTestMarker();

    @Override
    public void configureNettyBuilder(
        @NotNull NettyChannelBuilder builder,
        @NotNull GrpcClientsConfig clientsConfig,
        @NotNull GrpcClientConfig clientConfig
    ) {
        addCreateTrace(clientConfig.getName());
    }

    @Override
    public void configureShadedNettyBuilder(
        @NotNull io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder builder,
        @NotNull GrpcClientsConfig clientsConfig,
        @NotNull GrpcClientConfig clientConfig
    ) {
        addCreateTrace(clientConfig.getName());
    }

    private void addCreateTrace(String serverName) {
        @Nullable List<String> value = testMarker.getMark("createServer");
        if (value == null) {
            List<String> newList = new LinkedList<>();
            newList.add(serverName);
            testMarker.mark("createServer", newList);
        } else {
            value.add(serverName);
        }
    }

    public List<String> getCreateTraces() {
        return testMarker.getMark("createServer");
    }
}
