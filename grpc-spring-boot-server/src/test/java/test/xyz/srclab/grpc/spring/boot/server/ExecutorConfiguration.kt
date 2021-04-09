package test.xyz.srclab.grpc.spring.boot.server

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import xyz.srclab.spring.boot.task.ThreadPoolProperties
import xyz.srclab.spring.boot.task.newTaskExecutor

@Configuration
open class ExecutorConfiguration {

    @Bean
    open fun defaultExecutor(): TaskExecutor {
        val properties = ThreadPoolProperties()
        properties.threadNamePrefix = "default-task-executor"
        return newTaskExecutor(properties)
    }

    @Bean
    open fun server2Executor(): TaskExecutor {
        val properties = ThreadPoolProperties()
        properties.threadNamePrefix = "server2-task-executor"
        return newTaskExecutor(properties)
    }
}