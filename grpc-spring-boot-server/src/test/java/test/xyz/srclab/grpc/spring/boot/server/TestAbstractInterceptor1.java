package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import xyz.srclab.grpc.spring.boot.context.GrpcContext;
import xyz.srclab.grpc.spring.boot.server.GrpcServerInterceptor;
import xyz.srclab.grpc.spring.boot.server.interceptors.AbstractServerInterceptor;

@GrpcServerInterceptor(servicePatterns = "*3", order = 1)
public class TestAbstractInterceptor1 extends AbstractServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TestAbstractInterceptor1.class);

    @Override
    protected <ReqT, RespT> void intercept(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>intercept1: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), null);
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), null);
    }

    @Override
    protected <ReqT, RespT> void onReady(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>onReady1: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), TestConstants.CONTEXT_VALUE_1);
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), null);
    }

    @Override
    protected <ReqT, RespT> void onMessage(
        ReqT message,
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        context.set(TestConstants.CONTEXT_KEY_2, TestConstants.CONTEXT_VALUE_2);
        logger.info(">>>>onMessage1: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), TestConstants.CONTEXT_VALUE_1);
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), TestConstants.CONTEXT_VALUE_2);
    }

    @Override
    protected <ReqT, RespT> void onHalfClose(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>onHalfClose1: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), TestConstants.CONTEXT_VALUE_1);
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), TestConstants.CONTEXT_VALUE_2);
    }

    @Override
    protected <ReqT, RespT> void sendHeaders(
        Metadata sentHeaders,
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info("sendHeaders1: {}", sentHeaders);
        logger.info(">>>>sendHeaders1: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), TestConstants.CONTEXT_VALUE_1);
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), TestConstants.CONTEXT_VALUE_2);
    }

    @Override
    protected <ReqT, RespT> void sendMessage(
        RespT sentMessage,
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>sendMessage1: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), TestConstants.CONTEXT_VALUE_1);
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), TestConstants.CONTEXT_VALUE_2);
    }

    @Override
    protected <ReqT, RespT> void close(
        Status status,
        Metadata trailers,
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>close1: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), TestConstants.CONTEXT_VALUE_1);
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), TestConstants.CONTEXT_VALUE_2);
    }

    @Override
    protected <ReqT, RespT> void onCancel(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>onCancel1: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), TestConstants.CONTEXT_VALUE_1);
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), TestConstants.CONTEXT_VALUE_2);
    }

    @Override
    protected <ReqT, RespT> void onComplete(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        GrpcContext context) {
        logger.info(">>>>onComplete1: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), TestConstants.CONTEXT_VALUE_1);
        Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), TestConstants.CONTEXT_VALUE_2);
    }
}
