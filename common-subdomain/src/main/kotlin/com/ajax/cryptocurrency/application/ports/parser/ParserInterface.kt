package com.ajax.cryptocurrency.application.ports.parser

fun interface ParserInterface {
    fun savePrices(cryptocurrencyName: String)
}
