package com.ajax.cryptocurrency.service

import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.repository.impl.CryptocurrencyRepositoryImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File

@Service
class CryptocurrencyService(
    private val cryptocurrencyRepository: CryptocurrencyRepositoryImpl,
    @Value("\${cryptocurrency.name}") private val cryptocurrencies: List<String>,
) {
    fun findMinMaxPriceByCryptocurrencyName(name: String, sortOrder: Int): Mono<Cryptocurrency> =
        cryptocurrencyRepository.findMinMaxByName(name, sortOrder)

    fun findAll(): Flux<Cryptocurrency> = cryptocurrencyRepository.findAll()

    fun getCryptocurrencyPages(name: String?, pageNumber: Int, pageSize: Int): Flux<Cryptocurrency> {
        val pageable = PageRequest.of(pageNumber, pageSize, Sort.by("price"))
        return if (name == null)
            cryptocurrencyRepository.findAllBy(pageable)
        else {
            cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName(name, pageable)
        }
    }

    fun writeCsv(fileName: String): Mono<File> {
        val file = File("./$fileName.csv")
        return Flux.fromIterable(cryptocurrencies)
            .flatMap { name ->
                Mono.zip(
                    cryptocurrencyRepository.findMinMaxByName(name, 1),
                    cryptocurrencyRepository.findMinMaxByName(name, -1)
                ).map {
                    "$name,${it.t1.price},${it.t2.price}\n"
                }
            }
            .collectList()
            .map { list ->
                file.bufferedWriter().use { writer ->
                    writer.append("Cryptocurrency Name,Min Price,Max Price\n")
                    list.forEach { writer.append(it) }
                }
                file
            }
    }
}
