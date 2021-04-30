package xyz.srclab.grpc.spring.boot.server

import io.grpc.Server
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.SmartLifecycle
import xyz.srclab.common.run.runAsync
import javax.annotation.PostConstruct
import javax.annotation.Resource

open class GrpcServerLifecycle : SmartLifecycle {

    @Resource
    private lateinit var grpcServersProperties: GrpcServersProperties

    @Resource
    private lateinit var grpcServersFactory: GrpcServersFactory

    private lateinit var servers: Map<String, Server>
    private var isRunning: Boolean = false

    @PostConstruct
    private fun init() {
        servers = grpcServersFactory.create(
            grpcServersProperties.toServersConfig(), grpcServersProperties.toServerConfigs()
        )
    }

    override fun start() {
        for (serverEntry in servers) {
            val serverName = serverEntry.key
            val server = serverEntry.value
            server.start()
            logger.info(
                "gRPC server $serverName was started " +
                        "on ${server.listenSockets.joinToString()} " +
                        "with ${server.services.size} services"
            )
        }
        runAsync {
            for (serverEntry in servers) {
                val serverName = serverEntry.key
                val server = serverEntry.value
                try {
                    server.awaitTermination()
                } catch (e: Exception) {
                    logger.info("gRPC server $serverName was terminated by ", e)
                }
            }
        }
        isRunning = true
    }

    override fun stop() {
        for (serverEntry in servers) {
            val serverName = serverEntry.key
            val server = serverEntry.value
            logger.info("Stop gRPC server $serverName on ${server.listenSockets.joinToString()}")
            server.shutdownNow()
        }
        isRunning = false
    }

    override fun isRunning(): Boolean {
        return isRunning
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(GrpcServerLifecycle::class.java)
    }
}