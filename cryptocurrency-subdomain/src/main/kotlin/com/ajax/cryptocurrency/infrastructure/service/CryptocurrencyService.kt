package com.ajax.cryptocurrency.infrastructure.service

import com.ajax.cryptocurrency.application.ports.repository.CryptocurrencyRepositoryOutPort
import com.ajax.cryptocurrency.application.ports.service.CryptocurrencyServiceInPort
import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import com.ajax.cryptocurrency.infrastructure.database.redis.RedisCryptocurrencyRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File

@Service
class CryptocurrencyService(
    private val cryptocurrencyRepository: CryptocurrencyRepositoryOutPort,
    private val cryptocurrencyRedisRepository: RedisCryptocurrencyRepository,
    @Value("\${cryptocurrency.name}") private val cryptocurrencies: List<String>,
) : CryptocurrencyServiceInPort {

    override fun save(domainCryptocurrency: DomainCryptocurrency): Mono<DomainCryptocurrency> {
        return cryptocurrencyRepository.save(domainCryptocurrency)
            .doOnSuccess { cryptocurrency -> cryptocurrencyRedisRepository.save(cryptocurrency).subscribe() }
    }

    override fun findAll(): Flux<DomainCryptocurrency> {
        return cryptocurrencyRedisRepository.findAll()
            .switchIfEmpty(
                cryptocurrencyRepository.findAll()
                    .doOnNext { cryptocurrencyRedisRepository.save(it).subscribe() }
            )
    }
    override fun findMinMaxPriceByCryptocurrencyName(name: String, sortOrder: Int): Mono<DomainCryptocurrency> =
        cryptocurrencyRepository.findMinMaxByName(name, sortOrder)
    override fun getCryptocurrencyPages(name: String?, pageNumber: Int, pageSize: Int): Flux<DomainCryptocurrency> {
        val pageable = PageRequest.of(pageNumber, pageSize, Sort.by("price"))
        return if (name == null)
            cryptocurrencyRepository.findAllBy(pageable)
        else {
            cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName(name, pageable)
        }
    }

    override fun writeCsv(fileName: String): Mono<File> {
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
