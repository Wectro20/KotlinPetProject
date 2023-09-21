package com.ajax.cryptocurrency.nats

import cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyResponse
import cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyList
import com.ajax.cryptocurrency.NatsSubject.GET_MAX_CRYPTOCURRENCY_PAGES_SUBJECT
import com.ajax.cryptocurrency.service.CryptocurrencyService
import com.ajax.cryptocurrency.service.convertproto.CryptocurrencyConvertor
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class NatsCryptocurrencyPagesController(
    private val cryptocurrencyService: CryptocurrencyService,
    private val cryptocurrencyConvertor: CryptocurrencyConvertor,
    override val connection: Connection
) : NatsController<CryptocurrencyRequest, CryptocurrencyResponse> {

    override val subject: String = GET_MAX_CRYPTOCURRENCY_PAGES_SUBJECT

    override val parser: Parser<CryptocurrencyRequest> = CryptocurrencyRequest.parser()

    override fun handler(request: CryptocurrencyRequest): CryptocurrencyResponse {
        val cryptocurrencyPages = cryptocurrencyService.getCryptocurrencyPages(
                request.page.name,
                request.page.pageNumber,
                request.page.pageSize
        ).map { cryptocurrencyConvertor.cryptocurrencyToProto(it) }.collectList().block()

        val list = CryptocurrencyList.newBuilder()
            .addAllCryptocurrency(cryptocurrencyPages)
            .build()

        return CryptocurrencyResponse.newBuilder()
            .setCryptocurrencyList(list)
            .build()
    }
}
