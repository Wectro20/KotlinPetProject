package com.ajax.cryptocurrency

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CryptocurrencyApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<CryptocurrencyApplication>(*args)
}
