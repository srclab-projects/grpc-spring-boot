package test.xyz.srclab.spring.boot.grpc.server;

import io.grpc.stub.StreamObserver;
import xyz.srclab.spring.boot.grpc.server.GrpcService;
import xyz.srclab.spring.boot.proto.Group1HelloServiceGrpc;
import xyz.srclab.spring.boot.proto.HelloRequest;
import xyz.srclab.spring.boot.proto.HelloResponse;

@GrpcService
public class HelloService1 extends Group1HelloServiceGrpc.Group1HelloServiceImplBase {

    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        responseObserver.onNext(HelloResponse.newBuilder()
                .setMessage("HelloService1")
                .setThreadName(Thread.currentThread().getName())
                .build()
        );
        responseObserver.onCompleted();
    }
}
