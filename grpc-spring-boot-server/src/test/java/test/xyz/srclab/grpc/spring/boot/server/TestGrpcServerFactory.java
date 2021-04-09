package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.Server;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.test.TestMarker;
import xyz.srclab.grpc.spring.boot.server.DefaultGrpcServerFactory;
import xyz.srclab.grpc.spring.boot.server.GrpcServerDefinition;
import xyz.srclab.grpc.spring.boot.server.GrpcServiceDefinitionBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class TestGrpcServerFactory extends DefaultGrpcServerFactory {

    private final TestMarker testMarker = TestMarker.newTestMarker();

    @NotNull
    @Override
    public Server create(
            @NotNull GrpcServerDefinition serverDefinition,
            @NotNull Set<? extends GrpcServiceDefinitionBuilder> serviceBuilders
    ) {
        addCreateTrace(serverDefinition.getName());
        return super.create(serverDefinition, serviceBuilders);
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
