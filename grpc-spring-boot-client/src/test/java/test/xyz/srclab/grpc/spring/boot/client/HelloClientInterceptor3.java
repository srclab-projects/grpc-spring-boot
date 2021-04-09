package test.xyz.srclab.grpc.spring.boot.client;

import xyz.srclab.grpc.spring.boot.client.GrpcClientInterceptor;

@GrpcClientInterceptor("*3")
public class HelloClientInterceptor3 extends BaseClientInterceptor {
}
