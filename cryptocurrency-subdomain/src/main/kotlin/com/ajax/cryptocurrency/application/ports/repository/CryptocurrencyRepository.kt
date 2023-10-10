package com.ajax.cryptocurrency.application.ports.repository

import com.ajax.cryptocurrency.domain.CryptocurrencyDomain
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface CryptocurrencyRepository {
    fun save(cryptocurrencyDomain: CryptocurrencyDomain): Mono<CryptocurrencyDomain>

    fun findMinMaxByName(cryptocurrencyName: String, sort: Int): Mono<CryptocurrencyDomain>

    fun findAll(): Flux<CryptocurrencyDomain>

    fun findAllBy(pageable: Pageable): Flux<CryptocurrencyDomain>

    fun findCryptocurrencyPriceByCryptocurrencyName(name: String, pageable: Pageable): Flux<CryptocurrencyDomain>
}
