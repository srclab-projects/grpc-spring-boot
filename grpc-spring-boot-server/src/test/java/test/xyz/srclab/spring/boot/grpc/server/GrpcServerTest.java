package test.xyz.srclab.spring.boot.grpc.server;

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

    @Resource
    private DefaultHelloService defaultHelloService;

    @Resource
    private HelloService1 helloService1;

    @Resource
    private HelloService2 helloService2;

    @Resource
    private HelloService3 helloService3;

    @Resource
    private DefaultServerInterceptor defaultServerInterceptor;

    @Resource
    private GroupsServerInterceptor groupsServerInterceptor;

    @Resource
    private Group2ServerInterceptor group2ServerInterceptor;

    @Resource
    private Group3ServerInterceptor group3ServerInterceptor;

    @Test
    public void testServers() {
        HelloRequest helloRequest = HelloRequest.getDefaultInstance();
        Channel defaultChannel = NettyChannelBuilder.forTarget("127.0.0.1:6565").usePlaintext().build();
        Channel groupsChannel = NettyChannelBuilder.forTarget("127.0.0.1:6566").usePlaintext().build();
        Channel group2Channel = NettyChannelBuilder.forTarget("127.0.0.1:6567").usePlaintext().build();
        Channel group3Channel = NettyChannelBuilder.forTarget("127.0.0.1:6568").usePlaintext().build();
        Assert.assertEquals(
                DefaultHelloServiceGrpc.newBlockingStub(defaultChannel).hello(helloRequest).getMessage(),
                "DefaultHelloService"
        );
        Assert.assertEquals(
                Group1HelloServiceGrpc.newBlockingStub(defaultChannel).hello(helloRequest).getMessage(),
                "HelloService1"
        );
        Assert.assertEquals(
                Group2HelloServiceGrpc.newBlockingStub(groupsChannel).hello(helloRequest).getMessage(),
                "HelloService2"
        );
        Assert.assertEquals(
                Group3HelloServiceGrpc.newBlockingStub(groupsChannel).hello(helloRequest).getMessage(),
                "HelloService3"
        );
        Assert.assertEquals(
                Group2HelloServiceGrpc.newBlockingStub(group2Channel).hello(helloRequest).getMessage(),
                "HelloService2"
        );
        Assert.assertEquals(
                Group3HelloServiceGrpc.newBlockingStub(group3Channel).hello(helloRequest).getMessage(),
                "HelloService3"
        );

        Assert.expectThrows(StatusRuntimeException.class, () ->
                Group2HelloServiceGrpc.newBlockingStub(defaultChannel).hello(helloRequest));
        Assert.expectThrows(StatusRuntimeException.class, () ->
                Group3HelloServiceGrpc.newBlockingStub(defaultChannel).hello(helloRequest));
        Assert.expectThrows(StatusRuntimeException.class, () ->
                DefaultHelloServiceGrpc.newBlockingStub(groupsChannel).hello(helloRequest));
        Assert.expectThrows(StatusRuntimeException.class, () ->
                Group1HelloServiceGrpc.newBlockingStub(groupsChannel).hello(helloRequest));
        Assert.expectThrows(StatusRuntimeException.class, () ->
                Group3HelloServiceGrpc.newBlockingStub(group2Channel).hello(helloRequest));
        Assert.expectThrows(StatusRuntimeException.class, () ->
                Group2HelloServiceGrpc.newBlockingStub(group3Channel).hello(helloRequest));

        Assert.assertEquals(
                defaultServerInterceptor.getServiceTraces(),
                Arrays.asList(
                        "DefaultHelloService",
                        "Group1HelloService",
                        "Group2HelloService",
                        "Group3HelloService",
                        "Group2HelloService",
                        "Group3HelloService"
                )
        );
        Assert.assertEquals(
                groupsServerInterceptor.getServiceTraces(),
                Arrays.asList(
                        //"DefaultHelloService",
                        //"Group1HelloService",
                        "Group2HelloService",
                        "Group3HelloService",
                        "Group2HelloService",
                        "Group3HelloService"
                )
        );
        Assert.assertEquals(
                group2ServerInterceptor.getServiceTraces(),
                Arrays.asList(
                        //"DefaultHelloService",
                        //"Group1HelloService",
                        "Group2HelloService",
                        //"Group3HelloService",
                        "Group2HelloService"
                        //"Group3HelloService"
                )
        );
        Assert.assertEquals(
                group3ServerInterceptor.getServiceTraces(),
                Arrays.asList(
                        //"DefaultHelloService",
                        //"Group1HelloService",
                        //"Group2HelloService",
                        "Group3HelloService",
                        //"Group2HelloService",
                        "Group3HelloService"
                )
        );
    }
}
