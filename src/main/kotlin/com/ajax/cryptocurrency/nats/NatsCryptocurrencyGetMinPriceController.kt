package com.ajax.cryptocurrency.nats

import com.ajax.cryptocurrency.NatsSubject.GET_MIN_CRYPTOCURRENCY_PRICE_SUBJECT
import com.ajax.cryptocurrency.service.CryptocurrencyService
import com.ajax.cryptocurrency.service.convertproto.CryptocurrencyConvertor
import com.google.protobuf.Parser
import cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyResponse
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class NatsCryptocurrencyGetMinPriceController(
    private val cryptocurrencyService: CryptocurrencyService,
    private val cryptocurrencyConvertor: CryptocurrencyConvertor,
    override val connection: Connection
) : NatsController<CryptocurrencyRequest, CryptocurrencyResponse> {

    override val subject: String = GET_MIN_CRYPTOCURRENCY_PRICE_SUBJECT

    override val parser: Parser<CryptocurrencyRequest> = CryptocurrencyRequest.parser()

    override fun handler(request: CryptocurrencyRequest): CryptocurrencyResponse {
        val cryptocurrency = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName(request.name.name, 1).block()!!

        return CryptocurrencyResponse.newBuilder()
            .setCryptocurrency(cryptocurrencyConvertor.cryptocurrencyToProto(cryptocurrency))
            .build()
    }
}
