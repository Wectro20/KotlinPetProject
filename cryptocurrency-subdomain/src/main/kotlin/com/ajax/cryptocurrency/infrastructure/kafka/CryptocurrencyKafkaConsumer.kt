package com.ajax.cryptocurrency.infrastructure.kafka

import com.ajax.cryptocurrency.CryptocurrencyOuterClass.Cryptocurrency
import com.ajax.cryptocurrency.NatsSubject
import com.ajax.cryptocurrency.application.ports.kafka.KafkaConsumer
import io.nats.client.Connection
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import reactor.kafka.receiver.KafkaReceiver

@Service
class CryptocurrencyKafkaConsumer(
    private val kafkaReceiver: KafkaReceiver<String, Cryptocurrency>,
    private val natsService: Connection
) : KafkaConsumer {
    @PostConstruct
    override fun startListening() {
        kafkaReceiver.receive()
            .doOnNext { record ->
                val cryptocurrency = record.value()

                natsService.publish(NatsSubject.ADD_CRYPTOCURRENCY_SUBJECT, cryptocurrency.toByteArray())
            }
            .subscribe()
    }
}
