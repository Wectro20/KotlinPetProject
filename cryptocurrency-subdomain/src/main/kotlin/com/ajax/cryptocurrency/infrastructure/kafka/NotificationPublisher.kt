package com.ajax.cryptocurrency.infrastructure.kafka

import com.ajax.cryptocurrency.CryptocurrencyOuterClass
import com.ajax.cryptocurrency.KafkaTopic
import com.ajax.cryptocurrency.application.ports.NotificationPublisher
import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import com.ajax.cryptocurrency.infrastructure.convertproto.CryptocurrencyConvertor
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord

@Service
class NotificationPublisher(
    private val cryptocurrencyKafkaSender: KafkaSender<String, CryptocurrencyOuterClass.Cryptocurrency>,
    private val cryptocurrencyConvertor: CryptocurrencyConvertor
) : NotificationPublisher<DomainCryptocurrency> {
    override val topic = KafkaTopic.ADD_CRYPTOCURRENCY_TOPIC

    override fun publishNotification(domainObject: DomainCryptocurrency) {
        val cryptocurrencyProto = cryptocurrencyConvertor.cryptocurrencyToProto(domainObject)

        val senderRecord = SenderRecord.create(
            ProducerRecord(topic, domainObject.id, cryptocurrencyProto),
            null
        )

        cryptocurrencyKafkaSender.send(Mono.just(senderRecord))
            .subscribe()
    }
}
