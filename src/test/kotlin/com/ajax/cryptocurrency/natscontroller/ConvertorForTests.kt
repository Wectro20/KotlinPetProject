package com.ajax.cryptocurrency.natscontroller

import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.service.convertproto.toLocalDateTime
import com.ajax.cryptocurrency.service.convertproto.toTimestamp

fun Cryptocurrency.toProto(): CryptocurrencyOuterClass.Cryptocurrency {
    return CryptocurrencyOuterClass.Cryptocurrency.newBuilder()
        .setCryptocurrencyName(this.cryptocurrencyName)
        .setPrice(this.price)
        .setCreatedTime(this.createdTime.toTimestamp())
        .build()
}

fun CryptocurrencyOuterClass.Cryptocurrency.toDomain(): Cryptocurrency {
    return Cryptocurrency(
        cryptocurrencyName = this.cryptocurrencyName,
        price = this.price,
        createdTime = this.createdTime.toLocalDateTime()
    )
}
