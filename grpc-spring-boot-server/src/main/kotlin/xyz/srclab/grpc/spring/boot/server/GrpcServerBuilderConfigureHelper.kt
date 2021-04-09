package xyz.srclab.grpc.spring.boot.server

import io.grpc.ServerBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import org.springframework.context.ApplicationContext
import java.util.concurrent.Executor
import javax.annotation.Resource

open class GrpcServerBuilderConfigureHelper {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Resource
    private lateinit var grpcServerSslFactory: GrpcServerSslFactory

    open fun configureServices(
        builder: ServerBuilder<*>,
        serverDefinition: GrpcServerDefinition,
        serviceBuilders: Set<GrpcServiceDefinitionBuilder>,
    ) {
        for (serviceBuilder in serviceBuilders) {
            builder.addService(serviceBuilder.build())
        }
    }

    open fun configureExecutor(
        builder: ServerBuilder<*>,
        serverDefinition: GrpcServerDefinition,
    ) {
        val threadPoolBeanName = serverDefinition.threadPoolBeanName
        if (threadPoolBeanName === null) {
            return
        }
        val executor = applicationContext.getBean(threadPoolBeanName)
        if (executor !is Executor) {
            throw IllegalArgumentException("bean $threadPoolBeanName is not a Executor.")
        }
        builder.executor(executor)
    }

    open fun configureSsl(
        builder: NettyServerBuilder,
        serverDefinition: GrpcServerDefinition,
    ) {
        val sslContext = grpcServerSslFactory.create(serverDefinition)
        if (sslContext === null) {
            return
        }
        builder.sslContext(sslContext)
    }
}