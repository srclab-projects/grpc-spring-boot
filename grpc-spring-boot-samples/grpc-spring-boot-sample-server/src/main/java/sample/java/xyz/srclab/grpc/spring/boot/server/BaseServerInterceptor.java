package sample.java.xyz.srclab.grpc.spring.boot.server;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseServerInterceptor implements ServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(BaseServerInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        logger.info(">>>>interceptor: " + call.getMethodDescriptor());
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
}
