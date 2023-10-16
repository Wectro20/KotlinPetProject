package com.ajax.cryptocurrency.application.ports.repository

import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RedisCryptocurrencyRepositoryOutPort {
    fun save(domainCryptocurrency: DomainCryptocurrency): Mono<DomainCryptocurrency>
    fun findAll(): Flux<DomainCryptocurrency>
}
