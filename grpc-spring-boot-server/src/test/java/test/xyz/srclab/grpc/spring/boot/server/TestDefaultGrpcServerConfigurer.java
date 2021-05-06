package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.netty.NettyServerBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.test.TestMarker;
import xyz.srclab.grpc.spring.boot.server.DefaultGrpcServerConfigurer;
import xyz.srclab.grpc.spring.boot.server.GrpcServerConfig;
import xyz.srclab.grpc.spring.boot.server.GrpcServersConfig;

import java.util.LinkedList;
import java.util.List;

@Component
public class TestDefaultGrpcServerConfigurer implements DefaultGrpcServerConfigurer {

    private final TestMarker testMarker = TestMarker.newTestMarker();

    @Override
    public void configureNettyBuilder(
        @NotNull NettyServerBuilder builder,
        @NotNull GrpcServersConfig serversConfig,
        @NotNull GrpcServerConfig serverConfig
    ) {
        addCreateTrace(serverConfig.getName());
    }

    @Override
    public void configureShadedNettyBuilder(
        @NotNull io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder builder,
        @NotNull GrpcServersConfig serversConfig,
        @NotNull GrpcServerConfig serverConfig
    ) {
        addCreateTrace(serverConfig.getName());
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
