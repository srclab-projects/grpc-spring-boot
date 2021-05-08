package sample.java.xyz.srclab.grpc.spring.boot.server;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.srclab.grpc.spring.boot.context.GrpcContext;
import xyz.srclab.grpc.spring.boot.server.GrpcService;
import xyz.srclab.spring.boot.proto.HelloRequest;
import xyz.srclab.spring.boot.proto.HelloResponse;
import xyz.srclab.spring.boot.proto.HelloService3Grpc;

@GrpcService(serverPatterns = "server3")
public class HelloService3 extends HelloService3Grpc.HelloService3ImplBase {

    private static final Logger logger = LoggerFactory.getLogger(HelloService3.class);

    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        GrpcContext context = GrpcContext.current();
        logger.info("HelloService3.hello: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        //if (1 != 2) {
        //    throw new RuntimeException("ssss");
        //}
        responseObserver.onNext(HelloResponse.newBuilder()
            .setMessage("HelloService3")
            .setThreadName(Thread.currentThread().getName())
            .build()
        );
        logger.info("HelloService3.afterOnNext: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        responseObserver.onCompleted();
        logger.info("HelloService3.afterOnCompleted: {} | {}",
            context.getString(TestConstants.CONTEXT_KEY_1),
            context.getString(TestConstants.CONTEXT_KEY_2)
        );
        //throw new RuntimeException("ssss");
    }
}
