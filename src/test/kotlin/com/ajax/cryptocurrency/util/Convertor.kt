package com.ajax.cryptocurrency.util

import com.ajax.cryptocurrency.CryptocurrencyOuterClass.Cryptocurrency
import com.ajax.cryptocurrency.application.util.toLocalDateTime
import com.ajax.cryptocurrency.application.util.toTimestamp
import com.ajax.cryptocurrency.domain.DomainCryptocurrency

fun DomainCryptocurrency.cryptocurrencyToProto(): Cryptocurrency {
    return Cryptocurrency.newBuilder()
        .setCryptocurrencyName(this.cryptocurrencyName)
        .setPrice(this.price)
        .setCreatedTime(this.createdTime.toTimestamp())
        .build()
}

fun Cryptocurrency.protoToCryptocurrency(): DomainCryptocurrency {
    return DomainCryptocurrency(
        id = null,
        cryptocurrencyName = this.cryptocurrencyName,
        price = this.price,
        createdTime = this.createdTime.toLocalDateTime()
    )
}
