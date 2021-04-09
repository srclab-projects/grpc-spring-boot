package test.xyz.srclab.grpc.spring.boot.client;

import xyz.srclab.grpc.spring.boot.client.GrpcClientInterceptor;

@GrpcClientInterceptor({"*X", "*2", "*3"})
public class HelloClientInterceptorX extends BaseClientInterceptor {
}
