package test.xyz.srclab.grpc.spring.boot.client;

import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import xyz.srclab.spring.boot.proto.LbServiceGrpc;
import xyz.srclab.spring.boot.proto.RequestMessage;
import xyz.srclab.spring.boot.proto.ResponseMessage;

@Component
public class TestLbService extends LbServiceGrpc.LbServiceImplBase {

    @Override
    public void requestLb(RequestMessage request, StreamObserver<ResponseMessage> responseObserver) {
        responseObserver.onNext(ResponseMessage.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
