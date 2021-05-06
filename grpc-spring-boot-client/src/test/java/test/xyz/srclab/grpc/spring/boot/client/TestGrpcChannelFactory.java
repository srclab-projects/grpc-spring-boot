package test.xyz.srclab.grpc.spring.boot.client;

import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.test.TestMarker;
import xyz.srclab.grpc.spring.boot.client.DefaultGrpcChannelFactory;
import xyz.srclab.grpc.spring.boot.client.GrpcClientConfig;
import xyz.srclab.grpc.spring.boot.client.GrpcClientsConfig;

import java.util.LinkedList;
import java.util.List;

@Component
public class TestGrpcChannelFactory extends DefaultGrpcChannelFactory {

    private final TestMarker testMarker = TestMarker.newTestMarker();

    @NotNull
    @Override
    public Channel create(
        @NotNull GrpcClientsConfig grpcClientsConfig,
        @NotNull GrpcClientConfig grpcClientConfig,
        @NotNull List<? extends ClientInterceptor> interceptors
    ) {
        addCreateTrace(grpcClientConfig.getName());
        return super.create(grpcClientsConfig, grpcClientConfig, interceptors);
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
