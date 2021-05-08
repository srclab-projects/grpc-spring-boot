package sample.java.xyz.srclab.grpc.spring.boot.client;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseClientInterceptor implements ClientInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(BaseClientInterceptor.class);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
        MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        logger.info(">>>>interceptor: " + method);
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
                super.sendMessage(message);
            }
        };
    }
}
