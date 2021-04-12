package xyz.srclab.grpc.spring.boot.server

import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.netty.NettyServerBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import xyz.srclab.common.reflect.method
import java.lang.reflect.Method
import java.net.InetSocketAddress
import javax.annotation.PostConstruct
import javax.annotation.Resource
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder as ShadedNettyServerBuilder

import xyz.srclab.common.reflect.inheritanceSorted

open class DefaultGrpcServerFactory : GrpcServerFactory {

    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Resource
    private lateinit var grpcServerBuilderConfigureHelper: GrpcServerBuilderConfigureHelper

    private lateinit var grpcServerConfigurerTargetTypes: List<Class<*>>
    private lateinit var grpcServerConfigurerMap: Map<Class<*>, GrpcServerConfigurer<*>>

    @PostConstruct
    private fun init() {
        val shadedConfigurer = try {
            applicationContext.getBeansOfType(GrpcServerConfigurer::class.java)
        } catch (e: Exception) {
            null
        }

        fun Class<*>.findConfigureServerType(): Class<*> {
            return this.methods.find {
                it.name == "configureServerBuilder"
                        &&
                        it.parameterCount == 2
                        && ServerBuilder::class.java.isAssignableFrom(it.parameterTypes[0])
            }!!.parameterTypes[0]
        }

        grpcServerConfigurerTargetTypes = if (shadedConfigurer !== null) {
            val ss = shadedConfigurer.values.map { it.javaClass.findConfigureServerType() }.inheritanceSorted<Any>()
            ss
        } else {
            emptyList()
        }
    }

    override fun create(
        serverDefinition: GrpcServerDefinition,
        serviceBuilders: Set<GrpcServiceDefinitionBuilder>
    ): Server {
        return if (serverDefinition.inProcess) createInProcessServer(
            serverDefinition,
            serviceBuilders
        ) else createNettyServer(serverDefinition, serviceBuilders)
    }

    private fun createInProcessServer(
        serverDefinition: GrpcServerDefinition,
        serviceBuilders: Set<GrpcServiceDefinitionBuilder>
    ): Server {
        val builder = InProcessServerBuilder.forName(serverDefinition.name)
        grpcServerBuilderConfigureHelper.configureServices(
            builder,
            serverDefinition,
            serviceBuilders
        )
        logger.info("gRPC in-process-server created: ${serverDefinition.name}")
        return builder.build()
    }

    private fun createNettyServer(
        serverDefinition: GrpcServerDefinition,
        serviceGroupBuilders: Set<GrpcServiceDefinitionBuilder>
    ): Server {
        return if (serverDefinition.useShaded) createShadedNettyServer(
            serverDefinition,
            serviceGroupBuilders
        ) else createNettyServer(serverDefinition, serviceGroupBuilders)
    }

    private fun createCommonNettyServer(
        serverDefinition: GrpcServerDefinition,
        serviceGroupBuilders: Set<GrpcServiceDefinitionBuilder>
    ): Server {
        val builder = NettyServerBuilder.forAddress(InetSocketAddress(serverDefinition.host, serverDefinition.port))
        grpcServerBuilderConfigureHelper.configureServices(builder, serverDefinition, serviceGroupBuilders)
        grpcServerBuilderConfigureHelper.configureExecutor(builder, serverDefinition)
        grpcServerBuilderConfigureHelper.configureSsl(builder, serverDefinition)
        grpcServerBuilderConfigureHelper.configureServerMisc(builder, serverDefinition)

        //custom configure
        for (grpcShadedNettyServerConfigurer in grpcServerConfigurers) {
            grpcShadedNettyServerConfigurer.configureServerBuilder(builder, serverDefinition)
        }

        logger.info("gRPC netty-server created: ${serverDefinition.name}")
        return builder.build()
    }

    private fun createShadedNettyServer(
        serverDefinition: GrpcServerDefinition,
        serviceGroupBuilders: Set<GrpcServiceDefinitionBuilder>
    ): Server {
        val builder =
            ShadedNettyServerBuilder.forAddress(InetSocketAddress(serverDefinition.host, serverDefinition.port))
        grpcServerBuilderConfigureHelper.configureServices(builder, serverDefinition, serviceGroupBuilders)
        grpcServerBuilderConfigureHelper.configureExecutor(builder, serverDefinition)
        grpcServerBuilderConfigureHelper.configureSsl(builder, serverDefinition)
        grpcServerBuilderConfigureHelper.configureServerMisc(builder, serverDefinition)

        //custom configure
        for (grpcShadedNettyServerConfigurer in grpcServerConfigurers) {
            grpcShadedNettyServerConfigurer.configureShadedServerBuilder(builder, serverDefinition)
        }

        logger.info("gRPC shaded-netty-server created: ${serverDefinition.name}")
        return builder.build()
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(DefaultGrpcServerFactory::class.java)
    }
}