package test.xyz.srclab.grpc.spring.boot.client;

import io.grpc.Channel;
import io.grpc.Server;
import io.grpc.ServerInterceptors;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.Loaders;
import xyz.srclab.grpc.spring.boot.client.GrpcClient;
import xyz.srclab.grpc.spring.boot.client.GrpcStub;
import xyz.srclab.spring.boot.proto.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;

@SpringBootTest(
    classes = Starter.class
    //webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class GrpcClientTest extends AbstractTestNGSpringContextTests {

    private static final Logger logger = LoggerFactory.getLogger(GrpcClientTest.class);

    @GrpcClient("default")
    private Channel defaultChannel;

    @GrpcClient("client1")
    private Channel client1Channel;

    @GrpcClient("client2")
    private Channel client2Channel;

    @GrpcClient("client3")
    private Channel client3Channel;

    @GrpcClient
    private DefaultHelloServiceGrpc.DefaultHelloServiceBlockingStub defaultStub;

    @GrpcClient("client1")
    private HelloServiceXGrpc.HelloServiceXBlockingStub client1Stub;

    @GrpcClient("client2")
    private GrpcStub<HelloService2Grpc.HelloService2BlockingStub> client2Stub;

    @GrpcClient(clientName = "client3")
    private HelloService3Grpc.HelloService3BlockingStub client3Stub;

    @GrpcClient(clientName = "lb")
    private LbServiceGrpc.LbServiceBlockingStub lbStub;

    @Resource
    private DefaultClientInterceptor defaultClientInterceptor;

    @Resource
    private HelloClientInterceptorX helloClientInterceptorX;

    @Resource
    private HelloClientInterceptor2 helloClientInterceptor2;

    @Resource
    private HelloClientInterceptor3 helloClientInterceptor3;

    @Resource
    private TestGrpcChannelFactory testGrpcClientFactory;

    @Resource
    private TestDefaultGrpcChannelConfigurer testGrpcShadedNettyClientConfigurer;

    @Resource
    private TraceService traceService;

    @Resource
    private TestLbService testLbService;

    @Resource
    private TestLbInterceptor testLbInterceptor;

    @PostConstruct
    private void init() throws Exception {
        Server serverDefault = NettyServerBuilder
            .forPort(6565)
            .addService(new DefaultHelloService())
            .sslContext(
                GrpcSslContexts.forServer(
                    Loaders.loadResource("myServer.crt").openStream(),
                    Loaders.loadResource("myServer.key.pkcs8").openStream()
                )
                    .trustManager(Loaders.loadResource("myClient.crt").openStream())
                    .clientAuth(ClientAuth.REQUIRE)
                    .build()
            )
            .build();
        Server server1 = NettyServerBuilder.forPort(6566).addService(new HelloServiceX()).build();
        Server server2 = NettyServerBuilder.forPort(6567).addService(new HelloService2()).build();
        Server server3 = NettyServerBuilder.forPort(6568).addService(new HelloService3()).build();
        serverDefault.start();
        server1.start();
        server2.start();
        server3.start();


        Server lb1 = NettyServerBuilder
            .forPort(6666)
            .addService(
                ServerInterceptors.intercept(testLbService, testLbInterceptor)
            )
            .sslContext(
                GrpcSslContexts.forServer(
                    Loaders.loadResource("myServer.crt").openStream(),
                    Loaders.loadResource("myServer.key.pkcs8").openStream()
                )
                    .trustManager(Loaders.loadResource("myClient.crt").openStream())
                    .clientAuth(ClientAuth.REQUIRE)
                    .build()
            )
            .build();
        Server lb2 = NettyServerBuilder
            .forPort(6667)
            .addService(
                ServerInterceptors.intercept(testLbService, testLbInterceptor)
            )
            .sslContext(
                GrpcSslContexts.forServer(
                    Loaders.loadResource("myServer.crt").openStream(),
                    Loaders.loadResource("myServer.key.pkcs8").openStream()
                )
                    .trustManager(Loaders.loadResource("myClient.crt").openStream())
                    .clientAuth(ClientAuth.REQUIRE)
                    .build()
            )
            .build();
        lb1.start();
        lb2.start();
    }

    @Test
    public void testClients() throws Exception {
        HelloRequest helloRequest = HelloRequest.getDefaultInstance();

        /*
         * default: default
         * client1: interceptorX, default
         * client2: interceptor2, interceptorX, default
         * client3: interceptor3, interceptorX, default
         */

        Assert.assertEquals(
            DefaultHelloServiceGrpc.newBlockingStub(defaultChannel).hello(helloRequest).getMessage(),
            "DefaultHelloService"
        );
        Assert.assertEquals(
            defaultStub.hello(helloRequest).getMessage(),
            "DefaultHelloService"
        );

        Assert.assertEquals(
            HelloServiceXGrpc.newBlockingStub(client1Channel).hello(helloRequest).getMessage(),
            "HelloServiceX"
        );
        Assert.assertEquals(
            client1Stub.hello(helloRequest).getMessage(),
            "HelloServiceX"
        );

        Assert.assertEquals(
            HelloService2Grpc.newBlockingStub(client2Channel).hello(helloRequest).getMessage(),
            "HelloService2"
        );
        Assert.assertEquals(
            client2Stub.get().hello(helloRequest).getMessage(),
            "HelloService2"
        );

        Assert.assertEquals(
            HelloService3Grpc.newBlockingStub(client3Channel).hello(helloRequest).getMessage(),
            "HelloService3"
        );
        Assert.assertEquals(
            client3Stub.hello(helloRequest).getMessage(),
            "HelloService3"
        );

        Assert.assertEquals(
            defaultClientInterceptor.getServiceTraces(),
            Arrays.asList(
                "DefaultHelloService",
                "DefaultHelloService",
                "HelloServiceX",
                "HelloServiceX",
                "HelloService2",
                "HelloService2",
                "HelloService3",
                "HelloService3"
            )
        );
        Assert.assertEquals(
            helloClientInterceptorX.getServiceTraces(),
            Arrays.asList(
                //"DefaultHelloService",
                //"DefaultHelloService",
                "HelloServiceX",
                "HelloServiceX",
                "HelloService2",
                "HelloService2",
                "HelloService3",
                "HelloService3"
            )
        );
        Assert.assertEquals(
            helloClientInterceptor2.getServiceTraces(),
            Arrays.asList(
                //"DefaultHelloService",
                //"DefaultHelloService",
                //"HelloServiceX",
                //"HelloServiceX",
                "HelloService2",
                "HelloService2"
                //"HelloService3",
                //"HelloService3"
            )
        );
        Assert.assertEquals(
            helloClientInterceptor3.getServiceTraces(),
            Arrays.asList(
                //"DefaultHelloService",
                //"DefaultHelloService",
                //"HelloServiceX",
                //"HelloServiceX",
                //"HelloService2",
                //"HelloService2",
                "HelloService3",
                "HelloService3"
            )
        );

        Assert.assertEquals(
            traceService.getInterceptorTraces(),
            Arrays.asList(
                "HelloClientInterceptor2",
                "HelloClientInterceptorX",
                "DefaultClientInterceptor",
                "HelloClientInterceptor2",
                "HelloClientInterceptorX",
                "DefaultClientInterceptor"
            )
        );

        //Assert.assertEquals(
        //        defaultClientInterceptor.getThreadTraces(),
        //        Arrays.asList(
        //                "default-task-executor",
        //                "default-task-executor",
        //                "default-task-executor",
        //                "default-task-executor",
        //                "client2-task-executor",
        //                "client2-task-executor",
        //                "default-task-executor",
        //                "default-task-executor"
        //        )
        //);

        Assert.assertEquals(
            testGrpcClientFactory.getCreateTraces(),
            Arrays.asList(
                "default",
                "client1",
                "client2",
                "client3",
                "lb"
            )
        );

        Assert.assertEquals(
            testGrpcShadedNettyClientConfigurer.getCreateTraces(),
            Arrays.asList(
                "default",
                "client1",
                "client2",
                "client3",
                "lb"
            )
        );
    }

    @Test
    public void testLb() {
        for (int i = 0; i < 100; i++) {
            lbStub.requestLb(RequestMessage.getDefaultInstance());
        }
        logger.info("trace: {}", testLbInterceptor.getClientTrace());
        Assert.assertEquals(testLbInterceptor.getClientTrace().size(), 2);
    }
}
