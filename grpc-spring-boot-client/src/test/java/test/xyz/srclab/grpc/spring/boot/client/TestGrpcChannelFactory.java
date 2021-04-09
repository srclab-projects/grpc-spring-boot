package test.xyz.srclab.grpc.spring.boot.client;

import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.test.TestMarker;
import xyz.srclab.grpc.spring.boot.client.DefaultGrpcChannelFactory;
import xyz.srclab.grpc.spring.boot.client.GrpcClientDefinition;

import java.util.LinkedList;
import java.util.List;

@Component
public class TestGrpcChannelFactory extends DefaultGrpcChannelFactory {

    private final TestMarker testMarker = TestMarker.newTestMarker();

    @NotNull
    @Override
    public Channel create(
            @NotNull GrpcClientDefinition grpcClientDefinition,
            @NotNull List<? extends ClientInterceptor> interceptors
    ) {
        addCreateTrace(grpcClientDefinition.getName());
        return super.create(grpcClientDefinition, interceptors);
    }

    private void addCreateTrace(String serverName) {
        @Nullable List<String> value = testMarker.getMark("createTraces");
        if (value == null) {
            List<String> newList = new LinkedList<>();
            newList.add(serverName);
            testMarker.mark("createTraces", newList);
        } else {
            value.add(serverName);
        }
    }

    public List<String> getCreateTraces() {
        return testMarker.getMark("createTraces");
    }
}
