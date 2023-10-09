package com.ajax.cryptocurrency.application.ports.kafka

import com.ajax.cryptocurrency.domain.CryptocurrencyDomain

interface KafkaProducer<T>{
    val topic: String

    fun sendToKafka(domainObject: T)
}
