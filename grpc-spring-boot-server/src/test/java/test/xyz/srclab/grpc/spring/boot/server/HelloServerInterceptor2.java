package test.xyz.srclab.grpc.spring.boot.server;

import xyz.srclab.grpc.spring.boot.server.GrpcServerInterceptor;

@GrpcServerInterceptor("*2")
public class HelloServerInterceptor2 extends BaseServerInterceptor {
}
