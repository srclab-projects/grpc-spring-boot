package test.xyz.srclab.grpc.spring.boot.server;

import xyz.srclab.grpc.spring.boot.server.GrpcServerInterceptor;

@GrpcServerInterceptor({"group1", "group2", "group3"})
public class GroupsServerInterceptor extends BaseServerInterceptor {
}
