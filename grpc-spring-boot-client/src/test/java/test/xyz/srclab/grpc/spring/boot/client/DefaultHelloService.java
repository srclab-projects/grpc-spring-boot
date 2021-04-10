package test.xyz.srclab.grpc.spring.boot.client;

import io.grpc.stub.StreamObserver;
import xyz.srclab.spring.boot.proto.DefaultHelloServiceGrpc;
import xyz.srclab.spring.boot.proto.HelloRequest;
import xyz.srclab.spring.boot.proto.HelloResponse;

public class DefaultHelloService extends DefaultHelloServiceGrpc.DefaultHelloServiceImplBase {

    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        responseObserver.onNext(HelloResponse.newBuilder()
                .setMessage("DefaultHelloService")
                .setThreadName(Thread.currentThread().getName())
                .build()
        );
        responseObserver.onCompleted();
    }
}
