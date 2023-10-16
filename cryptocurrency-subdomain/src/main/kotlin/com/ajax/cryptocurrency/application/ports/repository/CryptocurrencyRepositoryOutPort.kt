package com.ajax.cryptocurrency.application.ports.repository

import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CryptocurrencyRepositoryOutPort {
    fun save(domainCryptocurrency: DomainCryptocurrency): Mono<DomainCryptocurrency>

    fun findMinMaxByName(cryptocurrencyName: String, sort: Int): Mono<DomainCryptocurrency>

    fun findAll(): Flux<DomainCryptocurrency>

    fun findAllBy(pageable: Pageable): Flux<DomainCryptocurrency>

    fun findCryptocurrencyPriceByCryptocurrencyName(name: String, pageable: Pageable): Flux<DomainCryptocurrency>
}
