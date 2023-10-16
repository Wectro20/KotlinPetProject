package com.ajax.cryptocurrency.infrastructure.nats

import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyList
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyResponse
import com.ajax.cryptocurrency.NatsSubject.GET_ALL_CRYPTOCURRENCIES_SUBJECT
import com.ajax.cryptocurrency.infrastructure.convertproto.CryptocurrencyConvertor
import com.ajax.cryptocurrency.application.nats.NatsController
import com.ajax.cryptocurrency.application.ports.service.CryptocurrencyServiceInPort
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class NatsCryptocurrencyGetAllController (
    private val cryptocurrencyServiceInPort: CryptocurrencyServiceInPort,
    private val cryptocurrencyConvertor: CryptocurrencyConvertor,
    override val connection: Connection
) : NatsController<CryptocurrencyRequest, CryptocurrencyResponse> {

    override val subject: String = GET_ALL_CRYPTOCURRENCIES_SUBJECT

    override val parser: Parser<CryptocurrencyRequest> = CryptocurrencyRequest.parser()

    override fun handler(request: CryptocurrencyRequest): Mono<CryptocurrencyResponse> {
        return cryptocurrencyServiceInPort.findAll()
            .map { cryptocurrencyConvertor.cryptocurrencyToProto(it) }
            .collectList()
            .map { allCryptocurrency ->
                val list = CryptocurrencyList.newBuilder()
                    .addAllCryptocurrency(allCryptocurrency)
                    .build()

                CryptocurrencyResponse.newBuilder()
                    .setCryptocurrencyList(list)
                    .build()
            }
    }
}
