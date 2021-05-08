package sample.java.xyz.srclab.grpc.spring.boot.server;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.srclab.grpc.spring.boot.context.GrpcContext;
import xyz.srclab.grpc.spring.boot.server.GrpcServerInterceptor;
import xyz.srclab.grpc.spring.boot.server.interceptors.AbstractServerInterceptor;

@GrpcServerInterceptor(servicePatterns = "*3", order = 2)
public class TestAbstractInterceptor2 extends AbstractServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TestAbstractInterceptor2.class);

    @Override
    protected <ReqT, RespT> void intercept(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        context.set(TestConstants.CONTEXT_KEY_1, TestConstants.CONTEXT_VALUE_1);
        logger.info(">>>>intercept2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    protected <ReqT, RespT> void onReady(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>onReady2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    protected <ReqT, RespT> void onMessage(
        ReqT message,
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>onMessage2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    protected <ReqT, RespT> void onHalfClose(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>onHalfClose2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    protected <ReqT, RespT> void sendHeaders(
        Metadata sentHeaders,
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info("sendHeaders1: {}", sentHeaders);
        logger.info(">>>>sendHeaders2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    protected <ReqT, RespT> void sendMessage(
        RespT sentMessage,
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>sendMessage2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    protected <ReqT, RespT> void close(
        Status status,
        Metadata trailers,
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>close2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    protected <ReqT, RespT> void onCancel(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>onCancel2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    protected <ReqT, RespT> void onComplete(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>onComplete2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    protected <ReqT, RespT> void onException(
        Throwable cause,
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>onException2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2),
            cause
        );
        call.close(Status.INTERNAL, headers);
    }
}
