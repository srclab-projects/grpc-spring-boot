package test.xyz.srclab.grpc.spring.boot.client;

import io.grpc.CallOptions;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.srclab.grpc.spring.boot.client.GrpcClientInterceptor;
import xyz.srclab.grpc.spring.boot.client.interceptors.SimpleClientInterceptor;
import xyz.srclab.grpc.spring.boot.context.GrpcContext;

@GrpcClientInterceptor(clientPatterns = "*3", order = 1)
public class TestSimpleInterceptor2 implements SimpleClientInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TestSimpleInterceptor2.class);

    @Override
    public <ReqT, RespT> void intercept(
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        GrpcContext context
    ) {
        context.set(TestConstants.CONTEXT_KEY_1, TestConstants.CONTEXT_VALUE_1);
        logger.info(">>>>Simple>>>>intercept2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        //Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), TestConstants.CONTEXT_VALUE_1);
        //Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), null);
    }

    @Override
    public <ReqT, RespT> void sendHeaders(
        Metadata sentHeader,
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        GrpcContext context
    ) {
        logger.info(">>>>Simple>>>>sendHeaders2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        //Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), TestConstants.CONTEXT_VALUE_1);
        //Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), null);
    }

    @Override
    public <ReqT, RespT> void sendMessage(
        ReqT sentMessage,
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        GrpcContext context
    ) {
        logger.info(">>>>Simple>>>>sendMessage2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        //Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), TestConstants.CONTEXT_VALUE_1);
        //Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), null);
    }

    @Override
    public <ReqT, RespT> void onReady(
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        GrpcContext context
    ) {
        logger.info(">>>>Simple>>>>onReady2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        //Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), TestConstants.CONTEXT_VALUE_1);
        //Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), TestConstants.CONTEXT_VALUE_2);
    }

    @Override
    public <ReqT, RespT> void onHeaders(
        Metadata headers,
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        GrpcContext context
    ) {
        logger.info(">>>>Simple>>>>onHeaders2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        //Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), TestConstants.CONTEXT_VALUE_1);
        //Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), TestConstants.CONTEXT_VALUE_2);
    }

    @Override
    public <ReqT, RespT> void onMessage(
        RespT message,
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        GrpcContext context
    ) {
        logger.info(">>>>Simple>>>>onMessage2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        //Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), TestConstants.CONTEXT_VALUE_1);
        //Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), TestConstants.CONTEXT_VALUE_2);
    }

    @Override
    public <ReqT, RespT> void onClose(
        Status status,
        Metadata trailers,
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        GrpcContext context
    ) {
        logger.info(">>>>Simple>>>>onClose2: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        //Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_1), TestConstants.CONTEXT_VALUE_1);
        //Assert.assertEquals(context.getString(TestConstants.CONTEXT_KEY_2), TestConstants.CONTEXT_VALUE_2);
    }
}
