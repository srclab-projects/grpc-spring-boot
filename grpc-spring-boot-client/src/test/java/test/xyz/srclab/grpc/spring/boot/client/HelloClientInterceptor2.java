package test.xyz.srclab.grpc.spring.boot.client;

import xyz.srclab.grpc.spring.boot.client.GrpcClientInterceptor;

@GrpcClientInterceptor("*2")
public class HelloClientInterceptor2 extends BaseClientInterceptor {
}
