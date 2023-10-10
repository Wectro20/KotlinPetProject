package com.ajax.cryptocurrency.application.mapper

import com.ajax.cryptocurrency.domain.CryptocurrencyDomain
import com.ajax.cryptocurrency.infrastructure.mongo.entity.CryptocurrencyEntity
import org.springframework.stereotype.Component

@Component
class CryptocurrencyMapper {
    fun toEntity(cryptocurrencyDomain: CryptocurrencyDomain): CryptocurrencyEntity {
        return CryptocurrencyEntity(
            cryptocurrencyName = cryptocurrencyDomain.cryptocurrencyName,
            price = cryptocurrencyDomain.price,
            createdTime = cryptocurrencyDomain.createdTime
        )
    }

    fun toDomain(cryptocurrencyEntity: CryptocurrencyEntity): CryptocurrencyDomain {
        return CryptocurrencyDomain(
            id = cryptocurrencyEntity.id,
            cryptocurrencyName = cryptocurrencyEntity.cryptocurrencyName,
            price = cryptocurrencyEntity.price,
            createdTime = cryptocurrencyEntity.createdTime
        )
    }
}
