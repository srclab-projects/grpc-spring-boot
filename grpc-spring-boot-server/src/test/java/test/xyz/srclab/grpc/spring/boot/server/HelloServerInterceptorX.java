package test.xyz.srclab.grpc.spring.boot.server;

import xyz.srclab.grpc.spring.boot.server.GrpcServerInterceptor;

@GrpcServerInterceptor({"*X", "*2", "*3"})
public class HelloServerInterceptorX extends BaseServerInterceptor {
}
