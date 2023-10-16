package com.ajax.cryptocurrency.infrastructure.convertproto

import com.ajax.cryptocurrency.CryptocurrencyOuterClass.Cryptocurrency
import com.ajax.cryptocurrency.application.util.toLocalDateTime
import com.ajax.cryptocurrency.application.util.toTimestamp
import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import org.springframework.stereotype.Component

@Component
class CryptocurrencyConvertor {
    fun cryptocurrencyToProto(
        domainCryptocurrency: DomainCryptocurrency
    ) : Cryptocurrency {
        return Cryptocurrency.newBuilder()
            .setCryptocurrencyName(domainCryptocurrency.cryptocurrencyName)
            .setPrice(domainCryptocurrency.price)
            .setCreatedTime(domainCryptocurrency.createdTime.toTimestamp())
            .build()
    }

    fun protoToCryptocurrency(
        cryptocurrencyProtoClass: Cryptocurrency
    ) : DomainCryptocurrency {
        return DomainCryptocurrency(
            id = null,
            cryptocurrencyName = cryptocurrencyProtoClass.cryptocurrencyName,
            price = cryptocurrencyProtoClass.price,
            createdTime = cryptocurrencyProtoClass.createdTime.toLocalDateTime()
        )
    }
}
