package com.ajax.cryptocurrency.application.config

import com.ajax.cryptocurrency.application.lib.ScheduledBackgroundJobStarter
import com.ajax.cryptocurrency.application.ports.parser.ParserInterface
import jakarta.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

@Component
class ScheduledBackgroundJobBeanPostProcessor(
    @Value("\${cryptocurrency.name}") val cryptocurrencyNames: List<String>,
) : BeanPostProcessor {
    private val beans = mutableMapOf<String, KClass<*>>()
    private val scheduler = Executors.newScheduledThreadPool(cryptocurrencyNames.size)

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val beanClass = bean::class
        beanClass.java.getAnnotation(ScheduledBackgroundJobStarter::class.java)?.let {
            beans[beanName] = beanClass
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        val originalBean = beans[beanName]
        if (originalBean != null) {
            val annotation = originalBean.java.getAnnotation(ScheduledBackgroundJobStarter::class.java)

            cryptocurrencyNames.forEach { cryptocurrencyName ->
                scheduler.scheduleAtFixedRate(
                    { (bean as ParserInterface).savePrices(cryptocurrencyName) },
                    annotation.startDelay, annotation.period, TimeUnit.MILLISECONDS
                )
            }
            cryptocurrencyNames.forEach { logger.info("Started parsing: {}", it) }
        }

        return bean
    }

    @PreDestroy
    fun shutdown() {
        scheduler.shutdownNow()
    }

    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
