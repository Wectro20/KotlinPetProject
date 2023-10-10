package com.ajax.cryptocurrency.infrastructure.shared.stream

import com.ajax.cryptocurrency.CryptocurrencyOuterClass.Cryptocurrency
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@Component
class SharedStream(@Value("\${cryptocurrency.name}") private val cryptocurrencyNames: List<String>) {
    private val processor: Sinks.Many<Cryptocurrency> = Sinks.many().multicast().onBackpressureBuffer()

    val cryptocurrencies: Flux<Cryptocurrency> = processor.asFlux().cache(cryptocurrencyNames.size)

    fun update(cryptocurrency: Cryptocurrency) {
        processor.tryEmitNext(cryptocurrency)
    }
}
