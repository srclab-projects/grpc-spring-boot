package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder as ShadedNettyServerBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.test.TestMarker;
import xyz.srclab.grpc.spring.boot.server.GrpcServerDefinition;
import xyz.srclab.grpc.spring.boot.server.GrpcServerConfigurer;

import java.util.LinkedList;
import java.util.List;

@Component
public class TestGrpcServerConfigurer implements GrpcServerConfigurer {

    private final TestMarker testMarker = TestMarker.newTestMarker();

    @Override
    public void configureShadedServerBuilder(@NotNull NettyServerBuilder builder, @NotNull GrpcServerDefinition serverDefinition) {
        addCreateTrace(serverDefinition.getName());
    }

    @Override
    public void configureServerBuilder(@NotNull NettyServerBuilder builder, @NotNull GrpcServerDefinition serverDefinition) {
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
