package sample.java.xyz.srclab.grpc.spring.boot.client;

import io.grpc.CallOptions;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.srclab.grpc.spring.boot.client.GrpcClientInterceptor;
import xyz.srclab.grpc.spring.boot.client.interceptors.AbstractClientInterceptor;
import xyz.srclab.grpc.spring.boot.context.GrpcContext;

@GrpcClientInterceptor(clientPatterns = "*3", order = 2)
public class TestAbstractInterceptor2 extends AbstractClientInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TestAbstractInterceptor2.class);

    @Override
    protected <ReqT, RespT> void intercept(
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        GrpcContext context
    ) {
        logger.info(">>>>intercept2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    protected <ReqT, RespT> void sendHeaders(
        Metadata sentHeader,
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        GrpcContext context
    ) {
        logger.info(">>>>sendHeaders2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    protected <ReqT, RespT> void sendMessage(
        ReqT sentMessage,
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        GrpcContext context
    ) {
        context.set(TestConstants.CONTEXT_KEY_2, TestConstants.CONTEXT_VALUE_2);
        logger.info(">>>>sendMessage2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    protected <ReqT, RespT> void onReady(
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        GrpcContext context
    ) {
        logger.info(">>>>onReady2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    protected <ReqT, RespT> void onHeaders(
        Metadata headers,
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        GrpcContext context
    ) {
        logger.info(">>>>onHeaders2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    protected <ReqT, RespT> void onMessage(
        RespT message,
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        GrpcContext context
    ) {
        logger.info(">>>>onMessage2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }

    @Override
    protected <ReqT, RespT> void onClose(
        Status status,
        Metadata trailers,
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        GrpcContext context
    ) {
        logger.info(">>>>onClose2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
    }
}
