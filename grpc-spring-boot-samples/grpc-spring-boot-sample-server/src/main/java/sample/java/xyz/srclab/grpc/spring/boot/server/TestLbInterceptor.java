package sample.java.xyz.srclab.grpc.spring.boot.server;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.srclab.grpc.spring.boot.server.GrpcServerInterceptor;

import java.util.TreeSet;

@GrpcServerInterceptor(servicePatterns = "*Lb*")
public class TestLbInterceptor implements ServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TestLbInterceptor.class);

    private final TreeSet<String> clientTrace = new TreeSet<>();

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        logger.info("authority: {}, attributes: {}", call.getAuthority(), call.getAttributes());
        clientTrace.add(call.getAttributes().get(Grpc.TRANSPORT_ATTR_LOCAL_ADDR).toString());
        return next.startCall(call, headers);
    }

    public TreeSet<String> getClientTrace() {
        return clientTrace;
    }
}
