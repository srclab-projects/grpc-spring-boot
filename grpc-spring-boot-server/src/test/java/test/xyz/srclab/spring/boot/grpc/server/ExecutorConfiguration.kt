package test.xyz.srclab.spring.boot.grpc.server

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
    open fun group2Executor(): TaskExecutor {
        val properties = ThreadPoolProperties()
        properties.threadNamePrefix = "group2-task-executor"
        return newTaskExecutor(properties)
    }
}