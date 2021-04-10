package test.xyz.srclab.grpc.spring.boot.client;

import io.grpc.stub.StreamObserver;
import xyz.srclab.spring.boot.proto.HelloRequest;
import xyz.srclab.spring.boot.proto.HelloResponse;
import xyz.srclab.spring.boot.proto.HelloService3Grpc;

public class HelloService3 extends HelloService3Grpc.HelloService3ImplBase {

    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        responseObserver.onNext(HelloResponse.newBuilder()
                .setMessage("HelloService3")
                .setThreadName(Thread.currentThread().getName())
                .build()
        );
        responseObserver.onCompleted();
    }
}
