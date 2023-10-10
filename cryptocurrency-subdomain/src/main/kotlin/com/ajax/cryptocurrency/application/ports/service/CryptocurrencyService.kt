package com.ajax.cryptocurrency.application.ports.service

import com.ajax.cryptocurrency.domain.CryptocurrencyDomain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File

interface CryptocurrencyService {
    fun findMinMaxPriceByCryptocurrencyName(name: String, sortOrder: Int): Mono<CryptocurrencyDomain>

    fun findAll(): Flux<CryptocurrencyDomain>

    fun getCryptocurrencyPages(name: String?, pageNumber: Int, pageSize: Int): Flux<CryptocurrencyDomain>

    fun writeCsv(fileName: String): Mono<File>
}
