package sample.java.xyz.srclab.grpc.spring.boot.server;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.srclab.grpc.spring.boot.context.GrpcContext;
import xyz.srclab.grpc.spring.boot.server.GrpcServerInterceptor;
import xyz.srclab.grpc.spring.boot.server.interceptors.SimpleServerInterceptor;

@GrpcServerInterceptor(servicePatterns = "*3", order = 2)
public class TestSimpleInterceptor2 implements SimpleServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TestSimpleInterceptor2.class);

    @Override
    public <ReqT, RespT> void intercept(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>Simple>>>>intercept2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    public <ReqT, RespT> void onReady(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>Simple>>>>onReady2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    public <ReqT, RespT> void onMessage(
        ReqT message,
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        context.set(TestConstants.CONTEXT_KEY_2, TestConstants.CONTEXT_VALUE_2);
        logger.info(">>>>Simple>>>>onMessage2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    public <ReqT, RespT> void onHalfClose(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>Simple>>>>onHalfClose2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    public <ReqT, RespT> void sendHeaders(
        Metadata sentHeaders,
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info("sendHeaders2: {}", sentHeaders);
        logger.info(">>>>Simple>>>>sendHeaders2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    public <ReqT, RespT> void sendMessage(
        RespT sentMessage,
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>Simple>>>>sendMessage2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    public <ReqT, RespT> void close(
        Status status,
        Metadata trailers,
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>Simple>>>>close2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    public <ReqT, RespT> void onCancel(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>Simple>>>>onCancel2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    public <ReqT, RespT> void onComplete(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>Simple>>>>onComplete2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    public <ReqT, RespT> void onException(
        Throwable cause,
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>Simple>>>>onException2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2),
            cause
        );
        call.close(Status.INTERNAL, headers);
    }
}
