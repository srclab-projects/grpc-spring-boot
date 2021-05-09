package sample.java.xyz.srclab.grpc.spring.boot.server;

import io.grpc.stub.StreamObserver;
import xyz.srclab.grpc.spring.boot.server.GrpcService;
import xyz.srclab.spring.boot.proto.LbServiceGrpc;
import xyz.srclab.spring.boot.proto.RequestMessage;
import xyz.srclab.spring.boot.proto.ResponseMessage;

@GrpcService(serverPatterns = "lb*")
public class TestLbService extends LbServiceGrpc.LbServiceImplBase {

    @Override
    public void requestLb(RequestMessage request, StreamObserver<ResponseMessage> responseObserver) {
        responseObserver.onNext(ResponseMessage.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
