package com.ajax.cryptocurrency.nats

import cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyResponse
import cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyList
import com.ajax.cryptocurrency.NatsSubject.GET_ALL_CRYPTOCURRENCIES_SUBJECT
import com.ajax.cryptocurrency.service.CryptocurrencyService
import com.ajax.cryptocurrency.service.convertproto.CryptocurrencyConvertor
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class NatsCryptocurrencyGetAllController (
    private val cryptocurrencyService: CryptocurrencyService,
    private val cryptocurrencyConvertor: CryptocurrencyConvertor,
    override val connection: Connection
) : NatsController<CryptocurrencyRequest, CryptocurrencyResponse> {

    override val subject: String = GET_ALL_CRYPTOCURRENCIES_SUBJECT

    override val parser: Parser<CryptocurrencyRequest> = CryptocurrencyRequest.parser()

    override fun handler(request: CryptocurrencyRequest): CryptocurrencyResponse {
        val allCryptocurrency = cryptocurrencyService.findAll().collectList().block()!!
            .map { cryptocurrencyConvertor.cryptocurrencyToProto(it) }

        val list = CryptocurrencyList.newBuilder()
            .addAllCryptocurrency(allCryptocurrency)
            .build()

        return CryptocurrencyResponse.newBuilder()
            .setCryptocurrencyList(list)
            .build()
    }
}
