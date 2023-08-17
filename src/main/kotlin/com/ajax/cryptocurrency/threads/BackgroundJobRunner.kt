package com.ajax.cryptocurrency.threads

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class BackgroundJobRunner (@Autowired private val priceSaver: PriceSaver,
                           @Value("\${cryptocurrency.name}") private val cryptocurrencyNames: List<String>) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val amountOfThreads = cryptocurrencyNames.size
        val threads = mutableListOf<Thread>()

        for (i in 1..amountOfThreads) {
            val thread = Thread(priceSaver)
            thread.start()
            threads.add(thread)
        }

        threads.forEach { it.join() }
    }
}