package test.xyz.srclab.spring.boot.grpc.server;

import xyz.srclab.spring.boot.grpc.server.GrpcServerInterceptor;

@GrpcServerInterceptor({"group1", "group2", "group3"})
public class GroupsServerInterceptor extends BaseServerInterceptor {
}
