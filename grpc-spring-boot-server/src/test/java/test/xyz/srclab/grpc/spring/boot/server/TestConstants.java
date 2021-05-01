package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.Context;

public class TestConstants {

    public static final Context.Key<String> CONTEXT_KEY = Context.key("testKey1");
}
