package com.ajax.cryptocurrency.infrastructure.nats

import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyList
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyResponse
import com.ajax.cryptocurrency.NatsSubject.GET_MAX_CRYPTOCURRENCY_PAGES_SUBJECT
import com.ajax.cryptocurrency.application.convertproto.CryptocurrencyConvertor
import com.ajax.cryptocurrency.application.nats.NatsController
import com.ajax.cryptocurrency.infrastructure.service.CryptocurrencyServiceImpl
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class NatsCryptocurrencyPagesController(
    private val cryptocurrencyServiceImpl: CryptocurrencyServiceImpl,
    private val cryptocurrencyConvertor: CryptocurrencyConvertor,
    override val connection: Connection
) : NatsController<CryptocurrencyRequest, CryptocurrencyResponse> {

    override val subject: String = GET_MAX_CRYPTOCURRENCY_PAGES_SUBJECT

    override val parser: Parser<CryptocurrencyRequest> = CryptocurrencyRequest.parser()

    override fun handler(request: CryptocurrencyRequest): Mono<CryptocurrencyResponse> {
        return cryptocurrencyServiceImpl.getCryptocurrencyPages(
            request.page.name,
            request.page.pageNumber,
            request.page.pageSize
        )
            .map { cryptocurrencyConvertor.cryptocurrencyToProto(it) }
            .collectList()
            .map { allCryptocurrency ->
                CryptocurrencyList.newBuilder()
                    .addAllCryptocurrency(allCryptocurrency)
                    .build()
            }
            .map { cryptocurrencyList ->
                CryptocurrencyResponse.newBuilder()
                    .setCryptocurrencyList(cryptocurrencyList)
                    .build()
            }
    }
}
