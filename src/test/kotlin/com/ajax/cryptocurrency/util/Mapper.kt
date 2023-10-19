package com.ajax.cryptocurrency.util

import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import com.ajax.cryptocurrency.infrastructure.database.mongo.entity.CryptocurrencyEntity

fun DomainCryptocurrency.toEntity(): CryptocurrencyEntity {
    return CryptocurrencyEntity(
        cryptocurrencyName = this.cryptocurrencyName,
        price = this.price,
        createdTime = this.createdTime
    )
}

fun CryptocurrencyEntity.toDomain(): DomainCryptocurrency {
    return DomainCryptocurrency(
        id = this.id.toHexString(),
        cryptocurrencyName = this.cryptocurrencyName,
        price = this.price,
        createdTime = this.createdTime
    )
}
