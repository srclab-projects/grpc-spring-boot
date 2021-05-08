package sample.java.xyz.srclab.grpc.spring.boot.server;

import xyz.srclab.grpc.spring.boot.server.GrpcServerInterceptor;

@GrpcServerInterceptor(value = {"*X", "*2", "*3"}, order = -1)
public class HelloServerInterceptorX extends BaseServerInterceptor {
}
