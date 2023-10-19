package com.ajax.cryptocurrency.application.ports.service

import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
interface CryptocurrencyServiceInPort {
    fun save(domainCryptocurrency: DomainCryptocurrency): Mono<DomainCryptocurrency>

    fun findMinMaxPriceByCryptocurrencyName(name: String, sortOrder: Int): Mono<DomainCryptocurrency>

    fun findAll(): Flux<DomainCryptocurrency>

    fun getCryptocurrencyPages(name: String?, pageNumber: Int, pageSize: Int): Flux<DomainCryptocurrency>

    fun writeCsv(fileName: String): Mono<File>
}
