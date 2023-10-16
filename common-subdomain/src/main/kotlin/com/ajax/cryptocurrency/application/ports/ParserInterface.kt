package com.ajax.cryptocurrency.application.ports

fun interface ParserInterface {
    fun savePrices(cryptocurrencyName: String)
}
