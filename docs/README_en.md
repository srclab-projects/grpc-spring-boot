# Spring Boot Starter for gRPC

<span id="author" class="author">Sun Qian</span>  
<span id="email" class="email"><fredsuvn@163.com></span>  

Table of Contents

-   [Introduction](#_introduction)
-   [Features](#_features)
-   [Getting](#_getting)
-   [Samples](#_samples)
-   [Usage](#_usage)
    -   [Server](#_server)
        -   [Create and start a server](#_create_and_start_a_server)
        -   [Multi-Servers](#_multi_servers)
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
        -   [Server Configuration Properties
            Table](#_server_configuration_properties_table)
    -   [Client](#_client)
        -   [Create a Client](#_create_a_client)
        -   [Multi-Clients:](#_multi_clients)
        -   [Load Balance](#_load_balance)
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
        -   [Client Configuration Properties
            Table](#_client_configuration_properties_table)
    -   [Web](#_web)
-   [Contribution and Contact](#_contribution_and_contact)
-   [License](#_license)

## Introduction

This is a concise, clear, easy to extend gRPC and high-customizable
spring-boot starter, with spring-boot style.

## Features

-   Simply use `@GrpcService`, `@GrpcServerInterceptor` and spring bean
    annotation such as `@Component` to register gRPC service and server
    interceptors;

-   Simply use `@GrpcClient`, `@GrpcClientInterceptor` and spring bean
    annotation such as `@Component` to register gRPC stub, channel and
    client interceptors;

-   Support multi-servers;

-   Provide simple way to client load-balance;

-   Fine-grained control for servers/clients and service and
    interceptors.

## Getting

Gradle

    implementation("xyz.srclab.grpc.spring.boot:grpc-spring-boot-starter-server:0.0.0")
    implementation("xyz.srclab.grpc.spring.boot:grpc-spring-boot-starter-client:0.0.0")
    implementation("xyz.srclab.grpc.spring.boot:grpc-spring-boot-starter-web:0.0.0")

Maven

    <dependencies>
      <dependency>
        <groupId>xyz.srclab.grpc.spring.boot</groupId>
        <artifactId>grpc-spring-boot-starter-server</artifactId>
        <version>0.0.0</version>
      </dependency>
      <dependency>
        <groupId>xyz.srclab.grpc.spring.boot</groupId>
        <artifactId>grpc-spring-boot-starter-client</artifactId>
        <version>0.0.0</version>
      </dependency>
      <dependency>
        <groupId>xyz.srclab.grpc.spring.boot</groupId>
        <artifactId>grpc-spring-boot-starter-web</artifactId>
        <version>0.0.0</version>
      </dependency>
    </dependencies>

Source Code

<https://github.com/srclab-projects/grpc-spring-boot>

## Samples

[grpc-spring-boot-samples](../grpc-spring-boot-samples/)

## Usage

### Server

#### Create and start a server

To create a gRPC server, first we add server properties in
application.yml (or .yaml, .properties):

    grpc:
      server:
        servers:
          server1:
            host: 127.0.0.1
            port: 6565

Now we have a gRPC server called `server1` with address:
`127.0.0.1:6565`. Then we add service on `server1`:

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

Now `server1` has a gRPC service `DefaultHelloService`. If we run the
application, `server1` will be auto-start.

#### Multi-Servers

If we need two servers that one on `6565` and another on `6566`, and
they share the host `localhost`:

    grpc:
      server:
        defaults:
          host: 127.0.0.1
        servers:
          server1:
            port: 6565
          server2:
            port: 6566

`defaults` property has same properties with each `server` property.
`server` properties will auto-inherit `defaults` properties which is not
overridden.

#### @GrpcService

By default, if a gRPC service class is annotated by `@Service` or other
spring-boot component annotation, it will work for all servers. Thus,
`DefaultHelloService` will work for both `server1` and `server2`. If we
want `DefaultHelloService` only works for `server1`:

    @GrpcService("server1")
    public class DefaultHelloService{}

    @GrpcService(serverPatterns = "server1")
    public class DefaultHelloService{}

    @GrpcService(serverPatterns = "*1")
    public class DefaultHelloService{}

`@GrpcService` can specify the servers which gRPC service works for, by
bean name declared on `value` or `serverPatterns`, and it supports
ant-pattern. Now `DefaultHelloService` only works for `server1`.

#### @GrpcServerInterceptor

Adding server interceptor is same with adding gRPC server:

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

`DefaultServerInterceptor` will work for all gRPC services
(`DefaultHelloService`), to limit it, use `@GrpcServerInterceptor`:

    @GrpcServerInterceptor(value = "*hello*", order = -2)
    public class DefaultServerInterceptor{}

    @GrpcServerInterceptor(servicePatterns = "*hello*", order = -3)
    public class DefaultServerInterceptor{}

Just like `@GrpcService`, `@GrpcServerInterceptor` can specify service
bean name pattern and support ant-pattern. The `order` property
specifies callback order, from low to high. Now
`DefaultServerInterceptor` only works for gRPC service whose bean name
matches `*hello*`.

#### AbstractServerInterceptor and SimpleServerInterceptor

`ServerInterceptor` is confusing (think about its nested calling,
callback execution order). For convenience this starter provides
`AbstractServerInterceptor` and `SimpleServerInterceptor`.

`AbstractServerInterceptor` is a skeletal implementation of
`ServerInterceptor`, provides serials of callback methods to override,
in simple order: intercept1 → intercept2 → onMessage2 → onMessage1
(detail see its javadoc).

`SimpleServerInterceptor` is an interface provides serials of callback
methods to override same with `AbstractServerInterceptor`.

Difference:

-   Each `AbstractServerInterceptor` is a `ServerInterceptor` instance
    but all `SimpleServerInterceptor` in a gRPC service will be merged
    to one `ServerInterceptor`;

-   Callback order is: intercept1 → intercept2 → onMessage1 → onMessage2
    (detail see its javadoc).

#### MetadataServerInterceptor

`MetadataServerInterceptor` is a simple ServerInterceptor to do with
metadata (headers).

#### DefaultGrpcServerConfigurer and DefaultGrpcServerConfigureHelper

By default, this starter uses `InProcessBuilder`, `NettyServerBuilder`
and `ShadedNettyServerBuilder` to create new gRPC server. If you want to
custom them, create a new bean of `DefaultGrpcServerConfigurer` and use
bean `DefaultGrpcServerConfigureHelper` to help.

#### GrpcServerFactory and DefaultGrpcServerFactory

This starter uses `GrpcServerFactory` to create a new gRPC server, its
default implementation is `DefaultGrpcServerFactory`. If you want to
custom this process, create a new bean of `GrpcServerFactory` to
instead.

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
<td class="content"><code>DefaultGrpcServerConfigurer</code> will invalid if you have a custom <code>GrpcServerFactory</code> bean, but <code>DefaultGrpcServerConfigureHelper</code> can be used still.</td>
</tr>
</tbody>
</table>

#### GrpcServersFactory and DefaultGrpcServersFactory

This starter uses `GrpcServersFactory` to create all gRPC server, its
default implementation is `DefaultGrpcServersFactory`. If you want to
custom this process, create a new bean of `GrpcServersFactory` to
instead.

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
<td class="content"><code>DefaultGrpcServerFactory</code> and <code>DefaultGrpcServerConfigurer</code> will invalid if you have a custom <code>GrpcServersFactory</code> bean, but <code>DefaultGrpcServerConfigureHelper</code> can be used still.</td>
</tr>
</tbody>
</table>

#### Server Configuration Properties Table

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

#### Create a Client

To create a gRPC client, first we add client properties in
application.yml (or .yaml, .properties):

    grpc:
      client:
        clients:
          client1:
            target: 127.0.0.1:6565

Now we have a gRPC client called `client1` with target:
`127.0.0.1:6565`. Then we add stub and channel on `client1`:

    public class TestBean {

        @GrpcClient
        private DefaultHelloServiceGrpc.DefaultHelloServiceBlockingStub stub1;

        @GrpcClient
        private Channel channel1;
    }

Now, `stub1` and `channel1` will be auto-wired with `client1`'s
properties when application starts.

#### Multi-Clients:

If we need two clients, for target `127.0.0.1:6565` and
`127.0.0.1:6566`:

    grpc:
      client:
        clients:
          client1:
            target: 127.0.0.1:6565
          client2:
            target: 127.0.0.1:6566

Then:

    public class TestBean {

        @GrpcClient
        private DefaultHelloServiceGrpc.DefaultHelloServiceBlockingStub defaultStub;

        @GrpcClient("client1")
        private HelloServiceXGrpc.HelloServiceXBlockingStub client1Stub;

        @GrpcClient("client2")
        private HelloService2Grpc.HelloService2BlockingStub client2Stub;
    }

If no client name specified on `@GrpcClient`, it will auto-wired with
first client name (here is `client1`).

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
<td class="content">Client configuration also inherit <code>defaults</code> properties like <a href="#_multi_servers">Multi-Servers</a>.</td>
</tr>
</tbody>
</table>

#### Load Balance

To set a load-balance target:

    grpc:
      client:
        clients:
          lb:
            target: lb:127.0.0.1/127.0.0.1:6666,127.0.0.1/127.0.0.1:6667

Now the client `lb` is load-balance.

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
<td class="content">load balance syntax is: <code>lb:authority1/host1:port1,authority2/host2:port2…​</code></td>
</tr>
</tbody>
</table>

#### ClientInterceptor

To declare a `ClientInterceptor`, just give a bean of
`ClientInterceptor` type:

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

Now we have a `DefaultClientInterceptor` as `ClientInterceptor` for all
client.

#### @ClientInterceptor

To specify interceptor work in fine-grained, use
`@GrpcServerInterceptor`:

    @GrpcClientInterceptor(value = "*2", order = 0)
    public class DefaultClientInterceptor{}

    @GrpcClientInterceptor(clientPatterns = "*2", order = -3)
    public class DefaultClientInterceptor{}

value or clientPatterns specifies which client
`DefaultClientInterceptor` work for, support ant-pattern. For now, it
only works for client whose bean name matches `\*2`.

#### AbstractClientInterceptor and SimpleClientInterceptor

`ClientInterceptor` is confusing (think about its nested calling,
callback execution order). For convenience this starter provides
`AbstractClientInterceptor` and `SimpleClientInterceptor`.

`AbstractClientInterceptor` is a skeletal implementation of
`ClientInterceptor`, provides serials of callback methods to override,
in simple order: intercept1 → intercept2 → onClose2 → onClose1 (detail
see its javadoc).

`SimpleClientInterceptor` is an interface provides serials of callback
methods to override same with `AbstractClientInterceptor`.

Difference:

-   Each `AbstractClientInterceptor` is a `ClientInterceptor` instance
    but all `SimpleClientInterceptor` in a gRPC channel will be merged
    to one `ClientInterceptor`;

-   Callback order is: intercept1 → intercept2 → onClose1 → onClose2
    (detail see its javadoc).

#### DefaultGrpcChannelConfigurer and DefaultGrpcChannelConfigureHelper

By default, this starter uses `InProcessBuilder`, `NettyServerBuilder`
and `ShadedNettyServerBuilder`, if you want to custom them, create a new
bean of `DefaultGrpcChannelConfigurer` and use bean
`DefaultGrpcChannelConfigureHelper` to help.

#### GrpcChannelFactory, DefaultGrpcChannelFactory, GrpcStubFactory and DefaultGrpcStubFactory

This starter uses `GrpcChannelFactory` to create a new gRPC stub, use
`GrpcStubFactory` to create a new gRPC channel. Default implementation
is `DefaultGrpcChannelFactory` and `DefaultGrpcStubFactory`. If you want
to custom this process, create a new bean of `GrpcChannelFactory` or
`GrpcStubFactory`.

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
<td class="content"><code>DefaultGrpcChannelConfigurer</code> will invalid if you have a custom <code>GrpcChannelFactory</code> bean, but <code>DefaultGrpcChannelConfigureHelper</code> can be used still.</td>
</tr>
</tbody>
</table>

#### GrpcTargetResolver and DefaultGrpcTargetResolver

This starter will register `LbNameResolverProvider` to resolve load
balance target (lb:authority/host1:port1,host2:port2…​). By default,
`LbNameResolverProvider` use `DefaultGrpcTargetResolver` to resolve, to
custom this process, create bean of `GrpcTargetResolver` to instead.

#### Client Configuration Properties Table

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

### Web

`grpc-spring-boot-starter-web` is used for making `Controller` support
protobuf `Message` type.

By default, it uses `Jackson2ObjectMapperBuilderCustomizer`.

## Contribution and Contact

-   <fredsuvn@163.com>

-   <https://github.com/srclab-projects/grpc-spring-boot>

-   QQ group: 1037555759

## License

[Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html)
