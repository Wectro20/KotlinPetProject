package com.ajax.cryptocurrency.infrastructure.kafka

import com.ajax.cryptocurrency.CryptocurrencyOuterClass
import com.ajax.cryptocurrency.KafkaTopic
import com.ajax.cryptocurrency.application.convertproto.CryptocurrencyConvertor
import com.ajax.cryptocurrency.domain.CryptocurrencyDomain
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord


@Service
class CryptocurrencyKafkaProducer(
    private val cryptocurrencyKafkaSender: KafkaSender<String, CryptocurrencyOuterClass.Cryptocurrency>,
    private val cryptocurrencyConvertor: CryptocurrencyConvertor
) {
    private val topic = KafkaTopic.ADD_CRYPTOCURRENCY_TOPIC

    fun sendCryptocurrencyToKafka(cryptocurrencyDomain: CryptocurrencyDomain) {
        val cryptocurrencyProto = cryptocurrencyConvertor.cryptocurrencyToProto(cryptocurrencyDomain)

        val senderRecord = SenderRecord.create(
            ProducerRecord(topic, cryptocurrencyDomain.id.toHexString(), cryptocurrencyProto),
            null
        )

        cryptocurrencyKafkaSender.send(Mono.just(senderRecord))
            .subscribe()
    }
}
