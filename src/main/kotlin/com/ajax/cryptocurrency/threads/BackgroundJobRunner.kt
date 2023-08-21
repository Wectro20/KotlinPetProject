package com.ajax.cryptocurrency.threads

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import kotlin.concurrent.thread

@Component
class BackgroundJobRunner (@Autowired private val priceSaver: PriceSaver,
                           @Value("\${cryptocurrency.name}") private val cryptocurrencyNames: List<String>) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val threads = cryptocurrencyNames.map {
            thread(start = true) {
                priceSaver.run()
            }
        }

        threads.forEach { it.join() }
    }
}