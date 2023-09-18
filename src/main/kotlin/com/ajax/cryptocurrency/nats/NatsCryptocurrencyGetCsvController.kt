package com.ajax.cryptocurrency.nats

import cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyResponse
import cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyFile
import com.ajax.cryptocurrency.NatsSubject.GET_CSV_CRYPTOCURRENCY_SUBJECT
import com.ajax.cryptocurrency.service.CryptocurrencyService
import com.google.protobuf.ByteString
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class NatsCryptocurrencyGetCsvController(
    private val cryptocurrencyService: CryptocurrencyService,
    override val connection: Connection
) : NatsController<CryptocurrencyRequest, CryptocurrencyResponse> {

    override val subject: String = GET_CSV_CRYPTOCURRENCY_SUBJECT

    override val parser: Parser<CryptocurrencyRequest> = CryptocurrencyRequest.parser()

    override fun handler(request: CryptocurrencyRequest): CryptocurrencyResponse {
        val cryptocurrencyFile = cryptocurrencyService.writeCsv(request.name.name)

        val fileBytes = ByteString.copyFrom(cryptocurrencyFile.readBytes())

        val cryptocurrencyResponse = CryptocurrencyFile.newBuilder().setFile(fileBytes)

        return CryptocurrencyResponse.newBuilder()
            .setFile(cryptocurrencyResponse)
            .build()
    }
}
