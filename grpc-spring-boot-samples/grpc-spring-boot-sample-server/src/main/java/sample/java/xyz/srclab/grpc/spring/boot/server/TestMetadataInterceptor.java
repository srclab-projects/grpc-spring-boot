package sample.java.xyz.srclab.grpc.spring.boot.server;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.srclab.grpc.spring.boot.server.GrpcServerInterceptor;
import xyz.srclab.grpc.spring.boot.server.interceptors.MetadataServerInterceptor;

@GrpcServerInterceptor(servicePatterns = "*3", order = 0)
public class TestMetadataInterceptor extends MetadataServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TestMetadataInterceptor.class);

    protected <ReqT, RespT> void doMetadata(ServerCall<ReqT, RespT> call, Metadata headers) {
        logger.info("headers: {}", headers);
    }
}
