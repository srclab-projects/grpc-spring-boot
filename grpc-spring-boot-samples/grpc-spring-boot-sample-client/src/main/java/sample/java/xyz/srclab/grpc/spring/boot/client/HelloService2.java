package sample.java.xyz.srclab.grpc.spring.boot.client;

import io.grpc.stub.StreamObserver;
import xyz.srclab.spring.boot.proto.HelloRequest;
import xyz.srclab.spring.boot.proto.HelloResponse;
import xyz.srclab.spring.boot.proto.HelloService2Grpc;

public class HelloService2 extends HelloService2Grpc.HelloService2ImplBase {

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
