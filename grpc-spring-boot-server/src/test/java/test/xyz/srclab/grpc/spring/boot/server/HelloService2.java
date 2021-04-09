package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.stub.StreamObserver;
import xyz.srclab.grpc.spring.boot.server.GrpcService;
import xyz.srclab.spring.boot.proto.HelloRequest;
import xyz.srclab.spring.boot.proto.HelloResponse;
import xyz.srclab.spring.boot.proto.HelloService2Grpc;

@GrpcService("server2")
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
