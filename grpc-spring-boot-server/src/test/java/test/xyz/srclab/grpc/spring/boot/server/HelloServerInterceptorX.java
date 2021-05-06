package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import xyz.srclab.grpc.spring.boot.server.GrpcServerInterceptor;

import java.util.Objects;

@GrpcServerInterceptor(value = {"*X", "*2", "*3"}, order = -1)
public class HelloServerInterceptorX extends BaseServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        if (Objects.equals(call.getMethodDescriptor().getServiceName(), "HelloService2")) {
            helloService2.addInterceptorTrace("HelloServerInterceptorX");
        }
        return super.interceptCall(call, headers, next);
    }
}
