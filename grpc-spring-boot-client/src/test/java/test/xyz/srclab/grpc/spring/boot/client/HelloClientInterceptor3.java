package test.xyz.srclab.grpc.spring.boot.client;

import xyz.srclab.grpc.spring.boot.client.GrpcClientInterceptor;

@GrpcClientInterceptor(value = "*3", order = -3)
public class HelloClientInterceptor3 extends BaseClientInterceptor {
}
