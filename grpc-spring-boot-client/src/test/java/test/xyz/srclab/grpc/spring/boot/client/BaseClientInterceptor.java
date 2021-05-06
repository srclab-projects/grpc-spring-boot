package test.xyz.srclab.grpc.spring.boot.client;

import io.grpc.*;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.test.TestMarker;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

public class BaseClientInterceptor implements ClientInterceptor {

    @Resource
    protected TraceService traceService;

    private final TestMarker testMarker = TestMarker.newTestMarker();

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
        MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        addServiceTrace(method.getServiceName());
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                super.start(
                    new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                        @Override
                        public void onHeaders(Metadata headers) {
                            super.onHeaders(headers);
                        }

                        @Override
                        public void onMessage(RespT message) {
                            super.onMessage(message);
                        }
                    },
                    headers
                );
            }

            @Override
            public void sendMessage(ReqT message) {
                addThreadTrace(Thread.currentThread().getName());
                super.sendMessage(message);
            }
        };
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

    private void addThreadTrace(String threadName) {
        @Nullable List<String> value = testMarker.getMark("threadTraces");
        if (value == null) {
            List<String> newList = new LinkedList<>();
            newList.add(threadName);
            testMarker.mark("threadTraces", newList);
        } else {
            value.add(threadName);
        }
    }

    public List<String> getThreadTraces() {
        return testMarker.getMark("threadTraces");
    }
}
