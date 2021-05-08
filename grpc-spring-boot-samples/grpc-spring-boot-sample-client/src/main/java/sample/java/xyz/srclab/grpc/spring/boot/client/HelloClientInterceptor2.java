package sample.java.xyz.srclab.grpc.spring.boot.client;

import xyz.srclab.grpc.spring.boot.client.GrpcClientInterceptor;

@GrpcClientInterceptor(value = "*2", order = -2)
public class HelloClientInterceptor2 extends BaseClientInterceptor {
}
