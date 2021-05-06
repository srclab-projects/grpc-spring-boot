package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.stub.StreamObserver;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.test.TestMarker;
import xyz.srclab.grpc.spring.boot.server.GrpcService;
import xyz.srclab.spring.boot.proto.HelloRequest;
import xyz.srclab.spring.boot.proto.HelloResponse;
import xyz.srclab.spring.boot.proto.HelloService2Grpc;

import java.util.LinkedList;
import java.util.List;

@GrpcService("server2")
public class HelloService2 extends HelloService2Grpc.HelloService2ImplBase {

    private final TestMarker testMarker = TestMarker.newTestMarker();

    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        responseObserver.onNext(HelloResponse.newBuilder()
            .setMessage("HelloService2")
            .setThreadName(Thread.currentThread().getName())
            .build()
        );
        responseObserver.onCompleted();
    }

    public void addInterceptorTrace(String interceptorName) {
        @Nullable List<String> value = testMarker.getMark("interceptorTraces");
        if (value == null) {
            List<String> newList = new LinkedList<>();
            newList.add(interceptorName);
            testMarker.mark("interceptorTraces", newList);
        } else {
            value.add(interceptorName);
        }
    }

    public List<String> getInterceptorTraces() {
        return testMarker.getMark("interceptorTraces");
    }
}
