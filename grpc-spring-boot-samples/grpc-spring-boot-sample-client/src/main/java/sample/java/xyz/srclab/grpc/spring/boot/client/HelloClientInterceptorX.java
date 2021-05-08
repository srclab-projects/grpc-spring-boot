package sample.java.xyz.srclab.grpc.spring.boot.client;

import xyz.srclab.grpc.spring.boot.client.GrpcClientInterceptor;

@GrpcClientInterceptor(value = {"*1", "*2", "*3"}, order = -1)
public class HelloClientInterceptorX extends BaseClientInterceptor {
}
