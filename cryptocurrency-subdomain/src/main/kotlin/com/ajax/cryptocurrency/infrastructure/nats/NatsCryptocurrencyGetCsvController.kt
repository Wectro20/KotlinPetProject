package com.ajax.cryptocurrency.infrastructure.nats

import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyResponse
import com.ajax.cryptocurrency.NatsSubject.GET_CSV_CRYPTOCURRENCY_SUBJECT
import com.ajax.cryptocurrency.application.nats.NatsController
import com.ajax.cryptocurrency.application.ports.service.CryptocurrencyServiceInPort
import com.google.protobuf.ByteString
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class NatsCryptocurrencyGetCsvController(
    private val cryptocurrencyServiceInPort: CryptocurrencyServiceInPort,
    override val connection: Connection
) : NatsController<CryptocurrencyRequest, CryptocurrencyResponse> {

    override val subject: String = GET_CSV_CRYPTOCURRENCY_SUBJECT

    override val parser: Parser<CryptocurrencyRequest> = CryptocurrencyRequest.parser()

    override fun handler(request: CryptocurrencyRequest): Mono<CryptocurrencyResponse> {
        return cryptocurrencyServiceInPort.writeCsv(request.name.name)
            .map { file -> ByteString.copyFrom(file.readBytes()) }
            .map { fileBytes ->
                CryptocurrencyResponse.newBuilder()
                    .apply { fileBuilder.setFile(fileBytes) }
                    .build()
            }
    }
}
