package test.xyz.srclab.grpc.spring.boot.server;

import xyz.srclab.grpc.spring.boot.server.GrpcServerInterceptor;

@GrpcServerInterceptor(groupPattern = "group3")
public class Group3ServerInterceptor extends BaseServerInterceptor {
}
