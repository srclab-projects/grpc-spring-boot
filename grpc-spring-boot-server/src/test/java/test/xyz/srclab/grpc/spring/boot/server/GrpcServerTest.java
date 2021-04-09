package test.xyz.srclab.grpc.spring.boot.server;

import io.grpc.Channel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.NettyChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.spring.boot.proto.*;

import javax.annotation.Resource;
import java.util.Arrays;

@SpringBootTest(
        classes = Starter.class
        //webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class GrpcServerTest extends AbstractTestNGSpringContextTests {

    private static final Logger logger = LoggerFactory.getLogger(GrpcServerTest.class);

    //@Resource
    //private DefaultHelloService defaultHelloService;
    //
    //@Resource
    //private HelloService1 helloService1;
    //
    //@Resource
    //private HelloService2 helloService2;
    //
    //@Resource
    //private HelloService3 helloService3;

    @Resource
    private DefaultServerInterceptor defaultServerInterceptor;

    @Resource
    private HelloServerInterceptorX helloServerInterceptorX;

    @Resource
    private HelloServerInterceptor2 helloServerInterceptor2;

    @Resource
    private HelloServerInterceptor3 helloServerInterceptor3;

    @Resource
    private TestGrpcServerFactory testGrpcServerFactory;

    @Test
    public void testServers() throws Exception {
        HelloRequest helloRequest = HelloRequest.getDefaultInstance();
        //ChannelCredentials cred = TlsChannelCredentials.newBuilder().build();
        Channel defaultChannel = NettyChannelBuilder.forTarget("localhost:6565").usePlaintext().build();
        //.sslContext(
        //        GrpcSslContexts.forClient()
        //                .keyManager(
        //                        Loaders.loadResource("myClient.crt").openStream(),
        //                        Loaders.loadResource("myClient.key.pkcs8").openStream()
        //                )
        //                .trustManager(
        //                        Loaders.loadResource("trusts.crt").openStream()
        //                )
        //                .clientAuth(ClientAuth.REQUIRE).build()
        //)
        //.build();
        Channel server1Channel = NettyChannelBuilder.forTarget("127.0.0.1:6566").usePlaintext().build();
        Channel server2Channel = NettyChannelBuilder.forTarget("127.0.0.1:6567").usePlaintext().build();
        Channel server3Channel = NettyChannelBuilder.forTarget("127.0.0.1:6568").usePlaintext().build();

        /*
         * default: default
         * server1: default, serverX
         * server2: default, serverX, server2
         * server3: default, serverX, server3
         */

        Assert.assertEquals(
                DefaultHelloServiceGrpc.newBlockingStub(defaultChannel).hello(helloRequest).getMessage(),
                "DefaultHelloService"
        );
        Assert.expectThrows(StatusRuntimeException.class, () ->
                HelloServiceXGrpc.newBlockingStub(defaultChannel).hello(helloRequest));
        Assert.expectThrows(StatusRuntimeException.class, () ->
                HelloService2Grpc.newBlockingStub(defaultChannel).hello(helloRequest));
        Assert.expectThrows(StatusRuntimeException.class, () ->
                HelloService3Grpc.newBlockingStub(defaultChannel).hello(helloRequest));

        Assert.assertEquals(
                DefaultHelloServiceGrpc.newBlockingStub(server1Channel).hello(helloRequest).getMessage(),
                "DefaultHelloService"
        );
        Assert.assertEquals(
                HelloServiceXGrpc.newBlockingStub(server1Channel).hello(helloRequest).getMessage(),
                "HelloServiceX"
        );
        Assert.expectThrows(StatusRuntimeException.class, () ->
                HelloService2Grpc.newBlockingStub(server1Channel).hello(helloRequest));
        Assert.expectThrows(StatusRuntimeException.class, () ->
                HelloService3Grpc.newBlockingStub(server1Channel).hello(helloRequest));


        Assert.assertEquals(
                DefaultHelloServiceGrpc.newBlockingStub(server2Channel).hello(helloRequest).getMessage(),
                "DefaultHelloService"
        );
        Assert.assertEquals(
                HelloServiceXGrpc.newBlockingStub(server2Channel).hello(helloRequest).getMessage(),
                "HelloServiceX"
        );
        Assert.assertEquals(
                HelloService2Grpc.newBlockingStub(server2Channel).hello(helloRequest).getMessage(),
                "HelloService2"
        );
        Assert.expectThrows(StatusRuntimeException.class, () ->
                HelloService3Grpc.newBlockingStub(server1Channel).hello(helloRequest));


        Assert.assertEquals(
                DefaultHelloServiceGrpc.newBlockingStub(server3Channel).hello(helloRequest).getMessage(),
                "DefaultHelloService"
        );
        Assert.assertEquals(
                HelloServiceXGrpc.newBlockingStub(server3Channel).hello(helloRequest).getMessage(),
                "HelloServiceX"
        );
        Assert.assertEquals(
                HelloService3Grpc.newBlockingStub(server3Channel).hello(helloRequest).getMessage(),
                "HelloService3"
        );
        Assert.expectThrows(StatusRuntimeException.class, () ->
                HelloService2Grpc.newBlockingStub(server1Channel).hello(helloRequest));

        Assert.assertEquals(
                defaultServerInterceptor.getServiceTraces(),
                Arrays.asList(
                        "DefaultHelloService",
                        "DefaultHelloService",
                        "HelloServiceX",
                        "DefaultHelloService",
                        "HelloServiceX",
                        "HelloService2",
                        "DefaultHelloService",
                        "HelloServiceX",
                        "HelloService3"
                )
        );
        Assert.assertEquals(
                helloServerInterceptorX.getServiceTraces(),
                Arrays.asList(
                        //"DefaultHelloService",
                        //"DefaultHelloService",
                        "HelloServiceX",
                        //"DefaultHelloService",
                        "HelloServiceX",
                        "HelloService2",
                        //"DefaultHelloService",
                        "HelloServiceX",
                        "HelloService3"
                )
        );
        Assert.assertEquals(
                helloServerInterceptor2.getServiceTraces(),
                Arrays.asList(
                        //"DefaultHelloService",
                        //"DefaultHelloService",
                        //"HelloServiceX",
                        //"DefaultHelloService",
                        //"HelloServiceX",
                        "HelloService2"
                        //"DefaultHelloService",
                        //"HelloServiceX",
                        //"HelloService3"
                )
        );
        Assert.assertEquals(
                helloServerInterceptor3.getServiceTraces(),
                Arrays.asList(
                        //"DefaultHelloService",
                        //"DefaultHelloService",
                        //"HelloServiceX",
                        //"DefaultHelloService",
                        //"HelloServiceX",
                        //"HelloService2",
                        //"DefaultHelloService",
                        //"HelloServiceX",
                        "HelloService3"
                )
        );

        Assert.assertTrue(
                DefaultHelloServiceGrpc.newBlockingStub(defaultChannel).hello(helloRequest).getThreadName()
                        .startsWith("default-task-executor")
        );
        Assert.assertTrue(
                HelloServiceXGrpc.newBlockingStub(server1Channel).hello(helloRequest).getThreadName()
                        .startsWith("default-task-executor")
        );
        Assert.assertTrue(
                HelloService2Grpc.newBlockingStub(server2Channel).hello(helloRequest).getThreadName()
                        .startsWith("group2-task-executor")
        );
        Assert.assertTrue(
                HelloService3Grpc.newBlockingStub(server3Channel).hello(helloRequest).getThreadName()
                        .startsWith("default-task-executor")
        );

        Assert.assertEquals(
                testGrpcServerFactory.getCreateTraces(),
                Arrays.asList(
                        "default",
                        "server1",
                        "server2",
                        "server3"
                )
        );
    }
}
