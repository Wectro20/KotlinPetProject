package com.ajax.cryptocurrency.infrastructure.mapper

import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import com.ajax.cryptocurrency.infrastructure.database.mongo.entity.CryptocurrencyEntity
import org.springframework.stereotype.Component

@Component
class CryptocurrencyMapper {
    fun toEntity(domainCryptocurrency: DomainCryptocurrency): CryptocurrencyEntity {
        return CryptocurrencyEntity(
            cryptocurrencyName = domainCryptocurrency.cryptocurrencyName,
            price = domainCryptocurrency.price,
            createdTime = domainCryptocurrency.createdTime
        )
    }

    fun toDomain(cryptocurrencyEntity: CryptocurrencyEntity): DomainCryptocurrency {
        return DomainCryptocurrency(
            id = cryptocurrencyEntity.id.toHexString(),
            cryptocurrencyName = cryptocurrencyEntity.cryptocurrencyName,
            price = cryptocurrencyEntity.price,
            createdTime = cryptocurrencyEntity.createdTime
        )
    }
}
