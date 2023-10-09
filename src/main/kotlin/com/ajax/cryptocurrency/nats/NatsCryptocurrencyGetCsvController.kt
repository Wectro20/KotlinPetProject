package com.ajax.cryptocurrency.nats

import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyFile
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyResponse
import com.ajax.cryptocurrency.NatsSubject.GET_CSV_CRYPTOCURRENCY_SUBJECT
import com.ajax.cryptocurrency.service.CryptocurrencyService
import com.google.protobuf.ByteString
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class NatsCryptocurrencyGetCsvController(
    private val cryptocurrencyService: CryptocurrencyService,
    override val connection: Connection
) : NatsController<CryptocurrencyRequest, CryptocurrencyResponse> {

    override val subject: String = GET_CSV_CRYPTOCURRENCY_SUBJECT

    override val parser: Parser<CryptocurrencyRequest> = CryptocurrencyRequest.parser()

    override fun handler(request: CryptocurrencyRequest): Mono<CryptocurrencyResponse> {
        return cryptocurrencyService.writeCsv(request.name.name)
            .flatMap { file -> Mono.fromCallable { ByteString.copyFrom(file.readBytes()) } }
            .map { fileBytes -> CryptocurrencyFile.newBuilder().setFile(fileBytes).build() }
            .map { cryptocurrencyResponse ->
                CryptocurrencyResponse.newBuilder().setFile(cryptocurrencyResponse).build()
            }
    }
}
