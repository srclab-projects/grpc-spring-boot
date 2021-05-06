package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DefaultServerInterceptor extends BaseServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        if (Objects.equals(call.getMethodDescriptor().getServiceName(), "HelloService2")) {
            helloService2.addInterceptorTrace("DefaultServerInterceptor");
        }
        return super.interceptCall(call, headers, next);
    }
}
