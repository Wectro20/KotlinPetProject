package com.ajax.cryptocurrency.service.convertproto

import cryptocurrency.CryptocurrencyOuterClass
import com.ajax.cryptocurrency.model.Cryptocurrency
import org.springframework.stereotype.Component

@Component
class CryptocurrencyConvertor {
    fun cryptocurrencyToProto(
        cryptocurrency: Cryptocurrency
    ) : CryptocurrencyOuterClass.Cryptocurrency {
        return CryptocurrencyOuterClass.Cryptocurrency.newBuilder()
            .setCryptocurrencyName(cryptocurrency.cryptocurrencyName)
            .setPrice(cryptocurrency.price)
            .setCreatedTime(cryptocurrency.createdTime.toTimestamp())
            .build()
    }

    fun protoToCryptocurrency(
        cryptocurrencyProtoClass: CryptocurrencyOuterClass.Cryptocurrency
    ) : Cryptocurrency {
        return Cryptocurrency(
            cryptocurrencyName = cryptocurrencyProtoClass.cryptocurrencyName,
            price = cryptocurrencyProtoClass.price,
            createdTime = cryptocurrencyProtoClass.createdTime.toLocalDateTime()
        )
    }
}
