package test.xyz.srclab.grpc.spring.boot.client;

import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.test.TestMarker;
import xyz.srclab.grpc.spring.boot.client.GrpcClientDefinition;
import xyz.srclab.grpc.spring.boot.client.GrpcShadedNettyChannelConfigurer;

import java.util.LinkedList;
import java.util.List;

@Component
public class TestGrpcShadedNettyChannelConfigurer implements GrpcShadedNettyChannelConfigurer {

    private final TestMarker testMarker = TestMarker.newTestMarker();

    @Override
    public void configureChannelBuilder(
            @NotNull NettyChannelBuilder builder, @NotNull GrpcClientDefinition clientDefinition) {
        addCreateTrace(clientDefinition.getName());
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
