package test.xyz.srclab.spring.boot.grpc.server;

import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;
import xyz.srclab.spring.boot.proto.DefaultHelloServiceGrpc;
import xyz.srclab.spring.boot.proto.HelloRequest;
import xyz.srclab.spring.boot.proto.HelloResponse;

@Service
public class DefaultHelloService extends DefaultHelloServiceGrpc.DefaultHelloServiceImplBase {

    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        responseObserver.onNext(HelloResponse.newBuilder().setMessage("DefaultHelloService").build());
        responseObserver.onCompleted();
    }
}
