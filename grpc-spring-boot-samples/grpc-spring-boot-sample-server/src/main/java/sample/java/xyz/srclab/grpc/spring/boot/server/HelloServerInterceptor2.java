package sample.java.xyz.srclab.grpc.spring.boot.server;

import xyz.srclab.grpc.spring.boot.server.GrpcServerInterceptor;

@GrpcServerInterceptor(value = "*2", order = -2)
public class HelloServerInterceptor2 extends BaseServerInterceptor {
}
