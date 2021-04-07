package test.xyz.srclab.spring.boot.grpc.server;

import xyz.srclab.spring.boot.grpc.server.GrpcServerInterceptor;

@GrpcServerInterceptor(groupPattern = "group3")
public class Group3ServerInterceptor extends BaseServerInterceptor {
}
