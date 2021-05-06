package test.xyz.srclab.grpc.spring.boot.client;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.MethodDescriptor;
import xyz.srclab.grpc.spring.boot.client.GrpcClientInterceptor;

import java.util.Objects;

@GrpcClientInterceptor(value = {"*1", "*2", "*3"}, order = -1)
public class HelloClientInterceptorX extends BaseClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
        MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        if (Objects.equals(method.getServiceName(), "HelloService2")) {
            traceService.addInterceptorTrace("HelloClientInterceptorX");
        }
        return super.interceptCall(method, callOptions, next);
    }
}
