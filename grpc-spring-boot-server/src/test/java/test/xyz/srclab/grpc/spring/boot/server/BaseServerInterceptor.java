package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.*;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.test.TestMarker;

import java.util.LinkedList;
import java.util.List;

public class BaseServerInterceptor implements ServerInterceptor {

    private final TestMarker testMarker = TestMarker.newTestMarker();

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        addServiceTrace(call.getMethodDescriptor().getServiceName());
        return next.startCall(
                new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                    @Override
                    public void sendMessage(RespT message) {
                        super.sendMessage(message);
                    }

                    @Override
                    public void sendHeaders(Metadata headers) {
                        super.sendHeaders(headers);
                    }

                    @Override
                    public void close(Status status, Metadata trailers) {
                        super.close(status, trailers);
                    }
                },
                headers
        );
    }

    private void addServiceTrace(String serviceName) {
        @Nullable List<String> value = testMarker.getMark("serviceTraces");
        if (value == null) {
            List<String> newList = new LinkedList<>();
            newList.add(serviceName);
            testMarker.mark("serviceTraces", newList);
        } else {
            value.add(serviceName);
        }
    }

    public List<String> getServiceTraces() {
        return testMarker.getMark("serviceTraces");
    }
}
