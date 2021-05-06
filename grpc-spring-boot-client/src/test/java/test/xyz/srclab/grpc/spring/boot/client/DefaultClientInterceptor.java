package test.xyz.srclab.grpc.spring.boot.client;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.MethodDescriptor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DefaultClientInterceptor extends BaseClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
        MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        if (Objects.equals(method.getServiceName(), "HelloService2")) {
            traceService.addInterceptorTrace("DefaultClientInterceptor");
        }
        return super.interceptCall(method, callOptions, next);
    }
}
