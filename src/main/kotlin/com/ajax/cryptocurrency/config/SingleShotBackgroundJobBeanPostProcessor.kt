package com.ajax.cryptocurrency.config

import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import com.ajax.cryptocurrency.threads.PriceSaver
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
class SingleShotBackgroundJobBeanPostProcessor(
    private val cryptocurrencyRepository: CryptocurrencyRepository,
    @Value("\${cryptocurrency.name}") val cryptocurrencyNames: List<String>,
    @Value("\${cryptocurrency.sleeptime}") val sleepTime: Long
) : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        val annotation = bean::class.java.getAnnotation(SingleShotBackgroundJob::class.java)

        if (annotation != null) {
            val startDelay = annotation.startDelay
            val maxParallelThreads = annotation.maxParallelThreads

            Executors.newScheduledThreadPool(1).schedule({
                val executor = Executors.newFixedThreadPool(maxParallelThreads)
                for (cryptocurrencyName in cryptocurrencyNames) {
                    executor.submit {
                        val priceSaver = PriceSaver(cryptocurrencyRepository)
                        priceSaver.executor = executor
                        priceSaver.cryptocurrencyName = cryptocurrencyName
                        priceSaver.interval = sleepTime
                        priceSaver.run()
                    }
                }
            }, startDelay, TimeUnit.MILLISECONDS)
        }
        return bean
    }
}
