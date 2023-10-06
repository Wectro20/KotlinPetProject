package com.ajax.cryptocurrency.nats

import com.ajax.cryptocurrency.CryptocurrencyOuterClass
import com.ajax.cryptocurrency.NatsSubject
import com.ajax.cryptocurrency.shared.stream.SharedStream
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class CryptocurrencyNatsListener(
    private val natsService: Connection,
    private val sharedStream: SharedStream
) {

    val parser: Parser<CryptocurrencyOuterClass.Cryptocurrency> = CryptocurrencyOuterClass.Cryptocurrency.parser()

    @PostConstruct
    fun startListening() {
        natsService.createDispatcher { message ->
            val parsedData = parser.parseFrom(message.data)
            sharedStream.update(parsedData)
        }.subscribe(NatsSubject.ADD_CRYPTOCURRENCY_SUBJECT)
    }
}
