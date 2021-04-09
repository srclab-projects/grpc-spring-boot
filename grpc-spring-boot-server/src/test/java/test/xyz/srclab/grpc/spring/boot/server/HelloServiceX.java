package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.stub.StreamObserver;
import xyz.srclab.grpc.spring.boot.server.GrpcService;
import xyz.srclab.spring.boot.proto.HelloRequest;
import xyz.srclab.spring.boot.proto.HelloResponse;
import xyz.srclab.spring.boot.proto.HelloServiceXGrpc;

@GrpcService("server*")
public class HelloServiceX extends HelloServiceXGrpc.HelloServiceXImplBase {

    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        responseObserver.onNext(HelloResponse.newBuilder()
                .setMessage("HelloServiceX")
                .setThreadName(Thread.currentThread().getName())
                .build()
        );
        responseObserver.onCompleted();
    }
}
