package com.ajax.cryptocurrency.bpp

import com.ajax.cryptocurrency.annotation.SingleShotBackgroundJob
import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import com.ajax.cryptocurrency.parser.PriceSaver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
class SingleShotBackgroundJobBeanPostProcessor(
    private val cryptocurrencyRepository: CryptocurrencyRepository,
    @Value("\${cryptocurrency.name}") val cryptocurrencyNames: List<String>,
    @Value("\${cryptocurrency.sleepTime}") private val sleepTime: String
) : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        val annotation = bean::class.java.getAnnotation(SingleShotBackgroundJob::class.java)

        if (annotation != null) {
            val startDelay = annotation.startDelay
            val maxParallelThreads = annotation.maxParallelThreads
            val scheduler = Executors.newScheduledThreadPool(maxParallelThreads)

            scheduler.schedule({
                for (cryptocurrencyName in cryptocurrencyNames) {
                    val priceSaver = PriceSaver(cryptocurrencyRepository, sleepTime)
                    priceSaver.scheduler = scheduler
                    priceSaver.cryptocurrencyName = cryptocurrencyName
                    priceSaver.startDelay = startDelay
                    priceSaver.task.run()
                }
            }, startDelay, TimeUnit.MILLISECONDS)
            cryptocurrencyNames.forEach { logger.info("Started parsing: {}", it) }
        }
        return bean
    }

    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
