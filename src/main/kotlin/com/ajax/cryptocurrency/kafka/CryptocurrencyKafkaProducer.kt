package com.ajax.cryptocurrency.kafka

import com.ajax.cryptocurrency.CryptocurrencyOuterClass
import com.ajax.cryptocurrency.KafkaTopic
import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.service.convertproto.CryptocurrencyConvertor
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

    fun sendCryptocurrencyToKafka(cryptocurrency: Cryptocurrency) {
        val cryptocurrencyProto = cryptocurrencyConvertor.cryptocurrencyToProto(cryptocurrency)

        val senderRecord = SenderRecord.create(
            ProducerRecord(topic, cryptocurrency.id.toHexString(), cryptocurrencyProto),
            null
        )

        cryptocurrencyKafkaSender.send(Mono.just(senderRecord))
            .subscribe()
    }
}
