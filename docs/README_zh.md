# <span class="image">![Boat Spring Boot](../logo.svg)</span> Boat Spring Boot: SrcLab核心基础Spring Boot库

<span id="author" class="author">Sun Qian</span>  
<span id="email" class="email"><fredsuvn@163.com></span>  

目录

-   [简介](#_简介)
-   [特性](#_特性)
-   [获取](#_获取)
-   [样例](#_样例)
-   [使用](#_使用)
    -   [Server](#_server)
        -   [创建一个服务](#_创建一个服务)
        -   [多Server服务](#_多server服务)
        -   [@GrpcService](#_grpcservice)
        -   [@GrpcServerInterceptor](#_grpcserverinterceptor)
        -   [AbstractServerInterceptor and
            SimpleServerInterceptor](#_abstractserverinterceptor_and_simpleserverinterceptor)
        -   [MetadataServerInterceptor](#_metadataserverinterceptor)
        -   [DefaultGrpcServerConfigurer and
            DefaultGrpcServerConfigureHelper](#_defaultgrpcserverconfigurer_and_defaultgrpcserverconfigurehelper)
        -   [GrpcServerFactory and
            DefaultGrpcServerFactory](#_grpcserverfactory_and_defaultgrpcserverfactory)
        -   [GrpcServersFactory and
            DefaultGrpcServersFactory](#_grpcserversfactory_and_defaultgrpcserversfactory)
        -   [Server配置属性表](#_server配置属性表)
    -   [Client](#_client)
        -   [创建一个客户端](#_创建一个客户端)
        -   [多客户端:](#_多客户端)
        -   [负载均衡](#_负载均衡)
        -   [ClientInterceptor](#_clientinterceptor)
        -   [@ClientInterceptor](#_clientinterceptor_2)
        -   [AbstractClientInterceptor and
            SimpleClientInterceptor](#_abstractclientinterceptor_and_simpleclientinterceptor)
        -   [DefaultGrpcChannelConfigurer and
            DefaultGrpcChannelConfigureHelper](#_defaultgrpcchannelconfigurer_and_defaultgrpcchannelconfigurehelper)
        -   [GrpcChannelFactory, DefaultGrpcChannelFactory,
            GrpcStubFactory and
            DefaultGrpcStubFactory](#_grpcchannelfactory_defaultgrpcchannelfactory_grpcstubfactory_and_defaultgrpcstubfactory)
        -   [GrpcTargetResolver and
            DefaultGrpcTargetResolver](#_grpctargetresolver_and_defaultgrpctargetresolver)
        -   [Client配置属性表](#_client配置属性表)
-   [Web](#_web)
-   [共享和联系方式](#_共享和联系方式)
-   [License](#_license)

## 简介

一个简洁, 清晰, 易扩展和高度可定制的spring-boot风格的spring-boot
starter.

## 特性

-   简单使用 `@GrpcService`, `@GrpcServerInterceptor` 和spring注解如
    `@Component` 来注册gRPC service和server interceptors;

-   简单使用 `@GrpcClient`, `@GrpcClientInterceptor` 和spring注解如
    `@Component` 来注册gRPC stub, channel以及client interceptors;

-   支持多server服务;

-   提供简单的方式进行客户端负载均衡;

-   细粒度的servers/clients以及它们的interceptors的控制.

## 获取

Gradle

    implementation("xyz.srclab.spring.boot.grpc:grpc-spring-boot-starter-server:0.0.0")
    implementation("xyz.srclab.spring.boot.grpc:grpc-spring-boot-starter-client:0.0.0")
    implementation("xyz.srclab.spring.boot.grpc:grpc-spring-boot-starter-web:0.0.0")

Maven

    <dependencies>
      <dependency>
        <groupId>xyz.srclab.spring.boot.grpc</groupId>
        <artifactId>grpc-spring-boot-starter-server</artifactId>
        <version>0.0.0</version>
      </dependency>
      <dependency>
        <groupId>xyz.srclab.spring.boot.grpc</groupId>
        <artifactId>grpc-spring-boot-starter-client</artifactId>
        <version>0.0.0</version>
      </dependency>
      <dependency>
        <groupId>xyz.srclab.spring.boot.grpc</groupId>
        <artifactId>grpc-spring-boot-starter-web</artifactId>
        <version>0.0.0</version>
      </dependency>
    </dependencies>

源代码

<https://github.com/srclab-projects/grpc-spring-boot>

## 样例

[grpc-spring-boot-samples](../grpc-spring-boot-samples/)

## 使用

### Server

#### 创建一个服务

我们首先在application.yml (或者 .yaml, .properties)添加server的属性:

    grpc:
      server:
        servers:
          server1:
            host: 127.0.0.1
            port: 6565

现在我们有了一个叫 `server1` 的服务, 在地址: `127.0.0.1:6565` 上.
接下来我们在 `server1` 上添加service:

    @Service
    public class DefaultHelloService extends DefaultHelloServiceGrpc.DefaultHelloServiceImplBase {

        @Override
        public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
            responseObserver.onNext(HelloResponse.newBuilder()
                .setMessage("DefaultHelloService")
                .setThreadName(Thread.currentThread().getName())
                .build()
            );
            responseObserver.onCompleted();
        }
    }

现在 `server1` 有了一个 gRPC service `DefaultHelloService`.
如果我们运行项目, `server1` 将会自动启动.

#### 多Server服务

如果我们需要在端口 `6565` 和 `6566` 上都开启服务, 并且他们共享主机
`localhost`:

    grpc:
      server:
        defaults:
          host: 127.0.0.1
        servers:
          server1:
            port: 6565
          server2:
            port: 6566

`defaults` 属性的子属性和 `server` 属性的子属性相同. `server`
属性将会自动注入 `defaults` 属性, 除了被覆盖配置的属性.

#### @GrpcService

默认情况下, 如果一个 gRPC service 类被 `@Service` 或者其他 spring-boot
注解所注释, 它将为所有的服务工作. 因此, `DefaultHelloService` 将同时为
`server1` 和 `server2` 工作. 如果我们希望 `DefaultHelloService` 只为
`server1` 工作:

    @GrpcService("server1")
    public class DefaultHelloService{}

    @GrpcService(serverPatterns = "server1")
    public class DefaultHelloService{}

    @GrpcService(serverPatterns = "*1")
    public class DefaultHelloService{}

`@GrpcService` 可以通过 `value` or `serverPatterns`
属性指定它愿意工作的服务名, 并且它支持 ant-pattern. 现在
`DefaultHelloService` 只为 `server1` 工作了.

#### @GrpcServerInterceptor

添加拦截器和添加服务一样:

    @Component
    public class DefaultServerInterceptor extends BaseServerInterceptor {

        @Override
        public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
            if (Objects.equals(call.getMethodDescriptor().getServiceName(), "HelloService2")) {
                helloService2.addInterceptorTrace("DefaultServerInterceptor");
            }
            return super.interceptCall(call, headers, next);
        }
    }

`DefaultServerInterceptor` 将会为所有的 gRPC services
(`DefaultHelloService`) 工作, 想要限制它, 使用 `@GrpcServerInterceptor`:

    @GrpcServerInterceptor(value = "*hello*", order = -2)
    public class DefaultServerInterceptor{}

    @GrpcServerInterceptor(servicePatterns = "*hello*", order = -3)
    public class DefaultServerInterceptor{}

就像 `@GrpcService`, `@GrpcServerInterceptor` 可以指定 service bean
的名字, 并且支持 ant-pattern. `order` 属性指定调用殊勋, 从低到高. 现在,
`DefaultServerInterceptor` 只为匹配 `*hello*` 的服务提供拦截了.

#### AbstractServerInterceptor and SimpleServerInterceptor

`ServerInterceptor` 足以令人困惑 (想想看它的嵌套调用和回调顺序).
为了方便开发, 这个starter 提供 `AbstractServerInterceptor` 和
`SimpleServerInterceptor`.

`AbstractServerInterceptor` 是 `ServerInterceptor` 的快捷实现,
提供一系列的回调方法以供重写, 并且有一个简单的调用顺序: intercept1 →
intercept2 → onMessage2 → onMessage1 (具体请参阅 javadoc).

`SimpleServerInterceptor` 是一个接口, 提供和 `AbstractServerInterceptor`
一样的回调方法一共重写.

区别:

-   每个 `AbstractServerInterceptor` 都是一个 `ServerInterceptor` 实例,
    但是所有的 `SimpleServerInterceptor` 对于每个 gRPC service
    都将被合并成一个 `ServerInterceptor`;

-   回调顺序是: intercept1 → intercept2 → onMessage1 → onMessage2
    (具体请参阅 javadoc).

#### MetadataServerInterceptor

`MetadataServerInterceptor` 是一个简单的 ServerInterceptor 用来处理
metadata (headers).

#### DefaultGrpcServerConfigurer and DefaultGrpcServerConfigureHelper

默认情况下, 这个starter使用 `InProcessBuilder`, `NettyServerBuilder` 和
`ShadedNettyServerBuilder` 来创建 gRPC server. 如果你想要定制这个过程,
创建一个 `DefaultGrpcServerConfigurer` bean 并且使用 bean
`DefaultGrpcServerConfigureHelper` 来辅助设置.

#### GrpcServerFactory and DefaultGrpcServerFactory

这个starter使用 `GrpcServerFactory` 来创建 gRPC server, 它默认的实现是
`DefaultGrpcServerFactory`. 如果你想要定制这个过程, 创建一个
`GrpcServerFactory` bean 来替代.

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Note
</div></td>
<td class="content"><code>DefaultGrpcServerConfigurer</code> 将会失效如果你创建了定制的 <code>GrpcServerFactory</code> bean, 但是 <code>DefaultGrpcServerConfigureHelper</code> 仍然可以使用.</td>
</tr>
</tbody>
</table>

#### GrpcServersFactory and DefaultGrpcServersFactory

这个starter使用 `GrpcServersFactory` 来创建所有的 gRPC server,
它默认的实现是 `DefaultGrpcServersFactory`. 如果你想要定制这个过程,
创建一个 `GrpcServersFactory` bean 来替代.

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Note
</div></td>
<td class="content"><code>DefaultGrpcServerFactory</code> 和 <code>DefaultGrpcServerConfigurer</code> 将会失效如果你创建了定制的 <code>GrpcServersFactory</code> bean, 但是`DefaultGrpcServerConfigureHelper` 仍然可以使用.</td>
</tr>
</tbody>
</table>

#### Server配置属性表

<table id="GrpcServersProperties" class="tableblock frame-all grid-all stretch">
<caption>Table 1. GrpcServersProperties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Key</th>
<th class="tableblock halign-left valign-top">Type</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">Comment</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>defaults</p></td>
<td class="tableblock halign-left valign-top"><p><a href="#ServerProperties">ServerProperties</a></p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>servers</p></td>
<td class="tableblock halign-left valign-top"><p>Map&lt;String, <a href="#ServerProperties">ServerProperties</a>&gt;</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>needGrpcAnnotation</p></td>
<td class="tableblock halign-left valign-top"><p>Boolean</p></td>
<td class="tableblock halign-left valign-top"><p>false</p></td>
<td class="tableblock halign-left valign-top"><p>Whether gRPC bean (<code>BindableService</code> and <code>ServerInterceptor</code>) should be annotated by gRPC annotation (<code>GrpcService</code> and <code>GrpcServerInterceptor</code>).</p>
<p>This means spring-boot annotation such as <code>@Component</code> is invalid for gRPC bean.</p>
<p>Default is false.</p></td>
</tr>
</tbody>
</table>

Table 1. GrpcServersProperties

<table id="ServerProperties" class="tableblock frame-all grid-all stretch">
<caption>Table 2. ServerProperties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Key</th>
<th class="tableblock halign-left valign-top">Type</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">Comment</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>inProcess</p></td>
<td class="tableblock halign-left valign-top"><p>Boolean</p></td>
<td class="tableblock halign-left valign-top"><p>false</p></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>useShaded</p></td>
<td class="tableblock halign-left valign-top"><p>Boolean</p></td>
<td class="tableblock halign-left valign-top"><p>false</p></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>host</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"><p>localhost</p></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>port</p></td>
<td class="tableblock halign-left valign-top"><p>Int</p></td>
<td class="tableblock halign-left valign-top"><p>6565</p></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>threadPoolBeanName</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Thread pool bean name for gRPC executor.</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>maxConcurrentCallsPerConnection</p></td>
<td class="tableblock halign-left valign-top"><p>Int</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>initialFlowControlWindow</p></td>
<td class="tableblock halign-left valign-top"><p>Int</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>flowControlWindow</p></td>
<td class="tableblock halign-left valign-top"><p>Int</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>maxMessageSize</p></td>
<td class="tableblock halign-left valign-top"><p>Int</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>maxHeaderListSize</p></td>
<td class="tableblock halign-left valign-top"><p>Int</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>keepAliveTimeInNanos</p></td>
<td class="tableblock halign-left valign-top"><p>Long</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>keepAliveTimeoutInNanos</p></td>
<td class="tableblock halign-left valign-top"><p>Long</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>maxConnectionIdleInNanos</p></td>
<td class="tableblock halign-left valign-top"><p>Long</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>maxConnectionAgeInNanos</p></td>
<td class="tableblock halign-left valign-top"><p>Long</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>maxConnectionAgeGraceInNanos</p></td>
<td class="tableblock halign-left valign-top"><p>Long</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>permitKeepAliveWithoutCalls</p></td>
<td class="tableblock halign-left valign-top"><p>Boolean</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>permitKeepAliveTimeInNanos</p></td>
<td class="tableblock halign-left valign-top"><p>Long</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>sslCertChainClassPath</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Same classpath and file properties are alternative and classpath first</p></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>sslPrivateKeyClassPath</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Same classpath and file properties are alternative and classpath first</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>sslTrustCertCollectionClassPath</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Same classpath and file properties are alternative and classpath first</p></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>sslCertChainFile</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Same classpath and file properties are alternative and classpath first</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>sslPrivateKeyFile</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Same classpath and file properties are alternative and classpath first</p></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>sslTrustCertCollectionFile</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Same classpath and file properties are alternative and classpath first</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>sslPrivateKeyPassword</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>sslClientAuth</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Auth enum with case-ignore: <code>none</code>, <code>optional</code> or <code>require</code>.</p>
<p>Default is <code>none</code>.</p></td>
</tr>
</tbody>
</table>

Table 2. ServerProperties

### Client

#### 创建一个客户端

我们首先在application.yml (或者 .yaml, .properties)添加client的属性:

    grpc:
      client:
        clients:
          client1:
            target: 127.0.0.1:6565

现在我们有了一个client叫 `client1`, target: `127.0.0.1:6565`.
接着我们使用 `client1` 添加stub和channel:

    public class TestBean {

        @GrpcClient
        private DefaultHelloServiceGrpc.DefaultHelloServiceBlockingStub stub1;

        @GrpcClient
        private Channel channel1;
    }

现在, `stub1` 和 `channel1` 在项目启动时将会使用 `client1`
的属性自动注入.

#### 多客户端:

如果我们需要两个客户端, 用target `127.0.0.1:6565` 和 `127.0.0.1:6566`:

    grpc:
      client:
        clients:
          client1:
            target: 127.0.0.1:6565
          client2:
            target: 127.0.0.1:6566

然后:

    public class TestBean {

        @GrpcClient
        private DefaultHelloServiceGrpc.DefaultHelloServiceBlockingStub defaultStub;

        @GrpcClient("client1")
        private HelloServiceXGrpc.HelloServiceXBlockingStub client1Stub;

        @GrpcClient("client2")
        private HelloService2Grpc.HelloService2BlockingStub client2Stub;
    }

如果client的名字在 `@GrpcClient` 上没有被指定,
它将使用配置的第一个客户端 (这里是 `client1`).

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Note
</div></td>
<td class="content">客户端配置也会继承 <code>defaults</code> 属性就像 <a href="#_多server服务">多Server服务</a>.</td>
</tr>
</tbody>
</table>

#### 负载均衡

设置 load-balance target:

    grpc:
      client:
        clients:
          lb:
            target: lb:127.0.0.1/127.0.0.1:6666,127.0.0.1:6667

现在, client `lb` 被配置成负载均衡了.

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Note
</div></td>
<td class="content">负载均衡的语法: <code>lb:authority/host1:port1,host2:port2…​</code></td>
</tr>
</tbody>
</table>

#### ClientInterceptor

申明一个 `ClientInterceptor` 只需要给一个 `ClientInterceptor`
类型的bean:

    @Component
    public class DefaultClientInterceptor extends BaseClientInterceptor {

        @Override
        public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
            if (Objects.equals(method.getServiceName(), "HelloService2")) {
                traceService.addInterceptorTrace("DefaultClientInterceptor");
            }
            return super.interceptCall(method, callOptions, next);
        }
    }

现在我们有了一个 `DefaultClientInterceptor` 作为 `ClientInterceptor`,
它将为所有的client服务.

#### @ClientInterceptor

要细粒度的设置拦截器, use `@GrpcServerInterceptor`:

    @GrpcClientInterceptor(value = "*2", order = 0)
    public class DefaultClientInterceptor{}

    @GrpcClientInterceptor(clientPatterns = "*2", order = -3)
    public class DefaultClientInterceptor{}

value 或者 clientPatterns 指定client `DefaultClientInterceptor`
为谁工作, 支持 ant-pattern. 现在, 它只为名字匹配 `\*2` 的client工作.

#### AbstractClientInterceptor and SimpleClientInterceptor

`ClientInterceptor` 足以令人困惑 (想想看它的嵌套调用和回调顺序).
为了方便开发, 这个starter 提供 `AbstractClientInterceptor` 和
`SimpleClientInterceptor`.

`AbstractClientInterceptor` 是 `ClientInterceptor` 的快捷实现,
提供一系列的回调方法以供重写, 并且有一个简单的调用顺序: intercept1 →
intercept2 → onClose2 → onClose1 (具体请参阅 javadoc).

`SimpleClientInterceptor` 是一个接口, 提供和 `AbstractClientInterceptor`
一样的回调方法一共重写.

区别:

-   每个 `AbstractClientInterceptor` 都是一个 `ClientInterceptor` 实例,
    但是所有的 `SimpleClientInterceptor` 对于每个 gRPC service
    都将被合并成一个 `ClientInterceptor`;

-   回调顺序是: intercept1 → intercept2 → onClose1 → onClose2
    (具体请参阅 javadoc).

#### DefaultGrpcChannelConfigurer and DefaultGrpcChannelConfigureHelper

默认情况下, 这个starter使用 `InProcessBuilder`, `NettyServerBuilder` 和
`ShadedNettyServerBuilder` 来创建 gRPC client. 如果你想要定制这个过程,
创建一个 `DefaultGrpcChannelConfigurer` bean 并且使用 bean
`DefaultGrpcChannelConfigureHelper` 来辅助设置.

#### GrpcChannelFactory, DefaultGrpcChannelFactory, GrpcStubFactory and DefaultGrpcStubFactory

这个starter使用 `GrpcChannelFactory` 来创建 gRPC stub, 使用
`GrpcStubFactory` 来创建 gRPC channel. 默认实现
`DefaultGrpcChannelFactory` 和 `DefaultGrpcStubFactory`.
如果你想要定制这个过程, 创建一个 `GrpcChannelFactory` bean 或者
`GrpcStubFactory` bean.

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Note
</div></td>
<td class="content"><code>DefaultGrpcChannelConfigurer</code> 将会失效如果你创建了定制的 <code>GrpcChannelFactory</code> bean, 但是 <code>DefaultGrpcChannelConfigureHelper</code> 仍然可以使用.</td>
</tr>
</tbody>
</table>

#### GrpcTargetResolver and DefaultGrpcTargetResolver

这个starter会注册 `LbNameResolverProvider` 来解析负载均衡的 target
(lb:authority/host1:port1,host2:port2…​). 默认情况下,
`LbNameResolverProvider` 使用 `DefaultGrpcTargetResolver` 来解析,
想要定制这个过程, 创建一个 `GrpcTargetResolver` bean 来替代.

#### Client配置属性表

<table id="GrpcClientsProperties" class="tableblock frame-all grid-all stretch">
<caption>Table 3. GrpcClientsProperties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Key</th>
<th class="tableblock halign-left valign-top">Type</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">Comment</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>defaults</p></td>
<td class="tableblock halign-left valign-top"><p><a href="#ClientProperties">ClientProperties</a></p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>servers</p></td>
<td class="tableblock halign-left valign-top"><p>Map&lt;String, <a href="#ClientProperties">ClientProperties</a>&gt;</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>needGrpcAnnotation</p></td>
<td class="tableblock halign-left valign-top"><p>Boolean</p></td>
<td class="tableblock halign-left valign-top"><p>false</p></td>
<td class="tableblock halign-left valign-top"><p>Whether gRPC bean <code>ClientInterceptor</code> should be annotated by gRPC annotation (<code>GrpcClientInterceptor</code>).</p>
<p>This means spring-boot annotation such as <code>@Component</code> is invalid for gRPC bean.</p>
<p>Default is false.</p></td>
</tr>
</tbody>
</table>

Table 3. GrpcClientsProperties

<table id="ClientProperties" class="tableblock frame-all grid-all stretch">
<caption>Table 4. ClientProperties</caption>
<colgroup>
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr class="header">
<th class="tableblock halign-left valign-top">Key</th>
<th class="tableblock halign-left valign-top">Type</th>
<th class="tableblock halign-left valign-top">Default</th>
<th class="tableblock halign-left valign-top">Comment</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>inProcess</p></td>
<td class="tableblock halign-left valign-top"><p>Boolean</p></td>
<td class="tableblock halign-left valign-top"><p>false</p></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>useShaded</p></td>
<td class="tableblock halign-left valign-top"><p>Boolean</p></td>
<td class="tableblock halign-left valign-top"><p>false</p></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>target</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"><p>localhost:6565</p></td>
<td class="tableblock halign-left valign-top"><p>Address or load balance (<code>lb:authority/host1:port1,host2:port2…​</code>)</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>threadPoolBeanName</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Thread pool bean name for gRPC executor.</p></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>initialFlowControlWindow</p></td>
<td class="tableblock halign-left valign-top"><p>Int</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>flowControlWindow</p></td>
<td class="tableblock halign-left valign-top"><p>Int</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>maxMessageSize</p></td>
<td class="tableblock halign-left valign-top"><p>Int</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>maxHeaderListSize</p></td>
<td class="tableblock halign-left valign-top"><p>Int</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>keepAliveTimeInNanos</p></td>
<td class="tableblock halign-left valign-top"><p>Long</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>keepAliveTimeoutInNanos</p></td>
<td class="tableblock halign-left valign-top"><p>Long</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>keepAliveWithoutCalls</p></td>
<td class="tableblock halign-left valign-top"><p>Boolean</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>deadlineAfterInNanos</p></td>
<td class="tableblock halign-left valign-top"><p>Long</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>loadBalancingPolicy</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"><p>round_robin</p></td>
<td class="tableblock halign-left valign-top"><p>Load balance policy: <code>round_robin</code>, <code>pick_first</code>.</p>
<p>Default is <code>round_robin</code>.</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>sslCertChainClassPath</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Same classpath and file properties are alternative and classpath first</p></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>sslPrivateKeyClassPath</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Same classpath and file properties are alternative and classpath first</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>sslTrustCertCollectionClassPath</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Same classpath and file properties are alternative and classpath first</p></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>sslCertChainFile</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Same classpath and file properties are alternative and classpath first</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>sslPrivateKeyFile</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Same classpath and file properties are alternative and classpath first</p></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>sslTrustCertCollectionFile</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Same classpath and file properties are alternative and classpath first</p></td>
</tr>
<tr class="even">
<td class="tableblock halign-left valign-top"><p>sslPrivateKeyPassword</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"></td>
</tr>
<tr class="odd">
<td class="tableblock halign-left valign-top"><p>sslClientAuth</p></td>
<td class="tableblock halign-left valign-top"><p>String</p></td>
<td class="tableblock halign-left valign-top"></td>
<td class="tableblock halign-left valign-top"><p>Auth enum with case-ignore: <code>none</code>, <code>optional</code> or <code>require</code>.</p>
<p>Default is <code>none</code>.</p></td>
</tr>
</tbody>
</table>

Table 4. ClientProperties

## Web

`grpc-spring-boot-starter-web` 用来让 `Controller` 支持protobuf的
`Message` 类型.

默认情况下, 它使用 `Jackson2ObjectMapperBuilderCustomizer` 来实现.

## 共享和联系方式

-   <fredsuvn@163.com>

-   <https://github.com/srclab-projects/grpc-spring-boot>

-   QQ group: 1037555759

## License

[Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html)
