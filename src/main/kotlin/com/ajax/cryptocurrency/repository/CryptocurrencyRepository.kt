package com.ajax.cryptocurrency.repository

import com.ajax.cryptocurrency.model.Cryptocurrency
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface CryptocurrencyRepository {
    fun save(cryptocurrency: Cryptocurrency): Mono<Cryptocurrency>

    fun findMinMaxByName(cryptocurrencyName: String, sort: Int): Mono<Cryptocurrency>

    fun findAll(): Flux<Cryptocurrency>

    fun findAllBy(pageable: Pageable): Flux<Cryptocurrency>

    fun findCryptocurrencyPriceByCryptocurrencyName(name: String, pageable: Pageable): Flux<Cryptocurrency>
}
