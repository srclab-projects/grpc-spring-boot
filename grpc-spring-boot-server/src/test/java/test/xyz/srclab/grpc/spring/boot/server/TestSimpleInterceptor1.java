package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.srclab.grpc.spring.boot.server.GrpcServerInterceptor;
import xyz.srclab.grpc.spring.boot.server.interceptors.SimpleServerInterceptor;

@GrpcServerInterceptor(servicePatterns = "*3", order = 1)
public class TestSimpleInterceptor1 implements SimpleServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TestSimpleInterceptor1.class);

    @Override
    public <ReqT, RespT> Context intercept(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        logger.info(">>>>intercept1");
        return null;
    }

    @Override
    public <ReqT> void onMessage(ReqT message, Metadata requestHeaders) {
        logger.info(">>>>onMessage1: {}", TestConstants.CONTEXT_KEY.get());
    }

    @Override
    public void onHalfClose(Metadata requestHeaders) {
        logger.info(">>>>onHalfClose1: {}", TestConstants.CONTEXT_KEY.get());
    }

    @Override
    public void onCancel(Metadata requestHeaders) {
        logger.info(">>>>onCancel1: {}", TestConstants.CONTEXT_KEY.get());
    }

    @Override
    public void onComplete(Metadata requestHeaders) {
        logger.info(">>>>onComplete1: {}", TestConstants.CONTEXT_KEY.get());
    }

    @Override
    public void onReady(Metadata requestHeaders) {
        logger.info(">>>>onReady1: {}", TestConstants.CONTEXT_KEY.get());
    }

    @Override
    public <RespT> void sendMessage(RespT message) {
        logger.info(">>>>sendMessage1: {}", TestConstants.CONTEXT_KEY.get());
    }

    @Override
    public void sendHeaders(Metadata headers) {
        logger.info(">>>>sendHeaders1: {}", TestConstants.CONTEXT_KEY.get());
    }

    @Override
    public void close(Status status, Metadata trailers) {
        logger.info(">>>>close1: {}", TestConstants.CONTEXT_KEY.get());
    }
}
