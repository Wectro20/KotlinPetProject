package com.ajax.cryptocurrency.shared.stream

import com.ajax.cryptocurrency.CryptocurrencyOuterClass.Cryptocurrency
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@Component
class SharedStream {
    private val processor: Sinks.Many<Cryptocurrency> = Sinks.many().multicast().onBackpressureBuffer()

    val flux: Flux<Cryptocurrency> = processor.asFlux().concatWith(Flux.never())

    fun update(cryptocurrency: Cryptocurrency) {
        processor.tryEmitNext(cryptocurrency)
    }
}
