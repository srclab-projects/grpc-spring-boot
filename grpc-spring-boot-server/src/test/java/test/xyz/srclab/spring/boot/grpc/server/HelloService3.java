package test.xyz.srclab.spring.boot.grpc.server;

import io.grpc.stub.StreamObserver;
import xyz.srclab.spring.boot.grpc.server.GrpcService;
import xyz.srclab.spring.boot.proto.Group3HelloServiceGrpc;
import xyz.srclab.spring.boot.proto.HelloRequest;
import xyz.srclab.spring.boot.proto.HelloResponse;

@GrpcService(group = "group3")
public class HelloService3 extends Group3HelloServiceGrpc.Group3HelloServiceImplBase {

    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        responseObserver.onNext(HelloResponse.newBuilder().setMessage("HelloService3").build());
        responseObserver.onCompleted();
    }
}
