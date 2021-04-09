package test.xyz.srclab.grpc.spring.boot.server;

import xyz.srclab.grpc.spring.boot.server.GrpcServerInterceptor;

@GrpcServerInterceptor(servicePatterns = "*3")
public class HelloServerInterceptor3 extends BaseServerInterceptor {
}
