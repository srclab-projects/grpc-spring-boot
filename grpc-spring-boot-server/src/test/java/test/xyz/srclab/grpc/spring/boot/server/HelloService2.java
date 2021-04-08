package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.stub.StreamObserver;
import xyz.srclab.grpc.spring.boot.server.GrpcService;
import xyz.srclab.spring.boot.proto.Group2HelloServiceGrpc;
import xyz.srclab.spring.boot.proto.HelloRequest;
import xyz.srclab.spring.boot.proto.HelloResponse;

@GrpcService("group2")
public class HelloService2 extends Group2HelloServiceGrpc.Group2HelloServiceImplBase {

    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        responseObserver.onNext(HelloResponse.newBuilder()
                .setMessage("HelloService2")
                .setThreadName(Thread.currentThread().getName())
                .build()
        );
        responseObserver.onCompleted();
    }
}
