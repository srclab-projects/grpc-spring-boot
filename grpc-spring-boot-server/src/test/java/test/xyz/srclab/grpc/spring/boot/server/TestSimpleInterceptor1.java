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
public class TestSimpleInterceptor1 implements SimpleServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TestSimpleInterceptor1.class);

    public <ReqT, RespT> void intercept(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        logger.info(">>>>intercept1");
    }

    public <ReqT> void onMessage(ReqT message) {
        logger.info(">>>>onMessage1");
    }

    public void onHalfClose() {
        logger.info(">>>>onHalfClose1");
    }

    public void onCancel() {
        logger.info(">>>>onCancel1");
    }

    public void onComplete() {
        logger.info(">>>>onComplete1");
    }

    public void onReady() {
        logger.info(">>>>onReady1");
    }

    public <RespT> void sendMessage(RespT message) {
        logger.info(">>>>sendMessage1");
    }

    public void sendHeaders(Metadata headers) {
        logger.info(">>>>sendHeaders1");
    }

    public void close(Status status, Metadata trailers) {
        logger.info(">>>>close1");
    }
}
