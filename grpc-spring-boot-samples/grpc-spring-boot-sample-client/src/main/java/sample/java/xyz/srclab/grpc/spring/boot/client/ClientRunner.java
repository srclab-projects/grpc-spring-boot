package sample.java.xyz.srclab.grpc.spring.boot.client;

import io.grpc.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import xyz.srclab.grpc.spring.boot.client.GrpcClient;
import xyz.srclab.grpc.spring.boot.client.GrpcStub;
import xyz.srclab.spring.boot.proto.*;

@Component
public class ClientRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ClientRunner.class);

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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        HelloRequest helloRequest = HelloRequest.getDefaultInstance();

        /*
         * default: default
         * client1: interceptorX, default
         * client2: interceptor2, interceptorX, default
         * client3: interceptor3, interceptorX, default
         */

        printMessage(
            "defaultChannel",
            DefaultHelloServiceGrpc.newBlockingStub(defaultChannel).hello(helloRequest).getMessage(),
            "DefaultHelloService"
        );
        printMessage(
            "defaultStub",
            defaultStub.hello(helloRequest).getMessage(),
            "DefaultHelloService"
        );

        printMessage(
            "client1Channel",
            HelloServiceXGrpc.newBlockingStub(client1Channel).hello(helloRequest).getMessage(),
            "HelloServiceX"
        );
        printMessage(
            "client1Stub",
            client1Stub.hello(helloRequest).getMessage(),
            "HelloServiceX"
        );

        printMessage(
            "client2Channel",
            HelloService2Grpc.newBlockingStub(client2Channel).hello(helloRequest).getMessage(),
            "HelloService2"
        );
        printMessage(
            "client2Stub",
            client2Stub.get().hello(helloRequest).getMessage(),
            "HelloService2"
        );

        printMessage(
            "client3Channel",
            HelloService3Grpc.newBlockingStub(client3Channel).hello(helloRequest).getMessage(),
            "HelloService3"
        );
        printMessage(
            "client3Stub",
            client3Stub.hello(helloRequest).getMessage(),
            "HelloService3"
        );
    }

    private void printMessage(String title, String result, String expected) {
        logger.info("{}: {}, expected: {}", title, result, expected);
    }
}
