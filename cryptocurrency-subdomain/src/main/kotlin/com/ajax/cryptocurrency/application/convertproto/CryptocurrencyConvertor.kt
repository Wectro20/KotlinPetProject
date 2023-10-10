package com.ajax.cryptocurrency.application.convertproto

import com.ajax.cryptocurrency.CryptocurrencyOuterClass.Cryptocurrency
import com.ajax.cryptocurrency.application.util.toLocalDateTime
import com.ajax.cryptocurrency.application.util.toTimestamp
import com.ajax.cryptocurrency.domain.CryptocurrencyDomain
import org.springframework.stereotype.Component

@Component
class CryptocurrencyConvertor {
    fun cryptocurrencyToProto(
        cryptocurrencyDomain: CryptocurrencyDomain
    ) : Cryptocurrency {
        return Cryptocurrency.newBuilder()
            .setCryptocurrencyName(cryptocurrencyDomain.cryptocurrencyName)
            .setPrice(cryptocurrencyDomain.price)
            .setCreatedTime(cryptocurrencyDomain.createdTime.toTimestamp())
            .build()
    }

    fun protoToCryptocurrency(
        cryptocurrencyProtoClass: Cryptocurrency
    ) : CryptocurrencyDomain {
        return CryptocurrencyDomain(
            cryptocurrencyName = cryptocurrencyProtoClass.cryptocurrencyName,
            price = cryptocurrencyProtoClass.price,
            createdTime = cryptocurrencyProtoClass.createdTime.toLocalDateTime()
        )
    }
}
