package com.ajax.cryptocurrency

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class CryptocurrencyApplication

fun main(args: Array<String>) {
	runApplication<CryptocurrencyApplication>(*args)
}
