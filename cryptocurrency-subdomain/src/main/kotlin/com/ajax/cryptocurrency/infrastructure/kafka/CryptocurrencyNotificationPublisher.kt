package com.ajax.cryptocurrency.infrastructure.kafka

import com.ajax.cryptocurrency.CryptocurrencyOuterClass
import com.ajax.cryptocurrency.KafkaTopic
import com.ajax.cryptocurrency.infrastructure.convertproto.CryptocurrencyConvertor
import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.jmx.export.notification.NotificationPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord


@Service
class CryptocurrencyNotificationPublisher(
    private val cryptocurrencyKafkaSender: KafkaSender<String, CryptocurrencyOuterClass.Cryptocurrency>,
    private val cryptocurrencyConvertor: CryptocurrencyConvertor
) {
    val topic = KafkaTopic.ADD_CRYPTOCURRENCY_TOPIC

    fun publishNotification(domainCryptocurrency: DomainCryptocurrency) {
        val cryptocurrencyProto = cryptocurrencyConvertor.cryptocurrencyToProto(domainCryptocurrency)

        val senderRecord = SenderRecord.create(
            ProducerRecord(topic, domainCryptocurrency.id, cryptocurrencyProto),
            null
        )

        cryptocurrencyKafkaSender.send(Mono.just(senderRecord))
            .subscribe()
    }
}
