package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import xyz.srclab.grpc.spring.boot.server.interceptors.SimpleServerInterceptor;

@Component
public class TestSimpleInterceptor2 implements SimpleServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TestSimpleInterceptor2.class);

    public <ReqT, RespT> void intercept(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        logger.info(">>>>intercept2");
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
