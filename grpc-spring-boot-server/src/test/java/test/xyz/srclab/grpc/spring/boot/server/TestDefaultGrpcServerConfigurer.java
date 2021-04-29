package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.netty.NettyServerBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.test.TestMarker;
import xyz.srclab.grpc.spring.boot.server.DefaultGrpcServerConfigurer;
import xyz.srclab.grpc.spring.boot.server.GrpcServerDefinition;

import java.util.LinkedList;
import java.util.List;

@Component
public class TestDefaultGrpcServerConfigurer implements DefaultGrpcServerConfigurer {

    private final TestMarker testMarker = TestMarker.newTestMarker();

    @Override
    public void configureNettyServerBuilder(
            @NotNull NettyServerBuilder builder, @NotNull GrpcServerDefinition serverDefinition) {
        addCreateTrace(serverDefinition.getName());
    }

    @Override
    public void configureShadedNettyServerBuilder(
            @NotNull io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder builder,
            @NotNull GrpcServerDefinition serverDefinition
    ) {
        addCreateTrace(serverDefinition.getName());
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
