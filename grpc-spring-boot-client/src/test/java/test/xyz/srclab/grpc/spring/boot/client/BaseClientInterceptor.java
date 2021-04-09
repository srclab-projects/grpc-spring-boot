package test.xyz.srclab.grpc.spring.boot.client;

import io.grpc.*;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.test.TestMarker;

import java.util.LinkedList;
import java.util.List;

public class BaseClientInterceptor implements ClientInterceptor {

    private final TestMarker testMarker = TestMarker.newTestMarker();

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        addServiceTrace(method.getServiceName());
        return next.newCall(method, callOptions);
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
