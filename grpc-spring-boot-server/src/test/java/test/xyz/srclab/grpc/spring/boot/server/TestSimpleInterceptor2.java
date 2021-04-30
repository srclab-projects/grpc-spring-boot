package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.srclab.grpc.spring.boot.server.GrpcServerInterceptor;
import xyz.srclab.grpc.spring.boot.server.interceptors.SimpleServerInterceptor;

@GrpcServerInterceptor(servicePatterns = "*3", order = 2)
public class TestSimpleInterceptor2 implements SimpleServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TestSimpleInterceptor2.class);

    @Override
    public <ReqT, RespT> Context intercept(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        logger.info(">>>>intercept2");
        return Context.current().withValue(Context.key("testKey1"), "testValue1");
    }

    public <ReqT> void onMessage(ReqT message) {
        logger.info(">>>>onMessage2");
    }

    public void onHalfClose() {
        logger.info(">>>>onHalfClose2");
    }

    public void onCancel() {
        logger.info(">>>>onCancel2");
    }

    public void onComplete() {
        logger.info(">>>>onComplete2");
    }

    public void onReady() {
        logger.info(">>>>onReady2");
    }

    public <RespT> void sendMessage(RespT message) {
        logger.info(">>>>sendMessage2");
    }

    public void sendHeaders(Metadata headers) {
        logger.info(">>>>sendHeaders2");
    }

    public void close(Status status, Metadata trailers) {
        logger.info(">>>>close2");
    }
}
