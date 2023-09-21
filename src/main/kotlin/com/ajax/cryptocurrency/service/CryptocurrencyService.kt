package com.ajax.cryptocurrency.service

import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import com.ajax.cryptocurrency.repository.impl.CryptocurrencyRepositoryImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Signal.subscribe
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
        return Mono.defer {
            val cryptoInfoMonoList = cryptocurrencies.map { name ->
                val minPriceMono = cryptocurrencyRepository.findMinMaxByName(name, 1)
                val maxPriceMono = cryptocurrencyRepository.findMinMaxByName(name, -1)

                minPriceMono.zipWith(maxPriceMono) { minPrice, maxPrice ->
                    Triple(name, minPrice.price, maxPrice.price)
                }
            }


            val combinedFlux = Flux.zip(cryptoInfoMonoList) { array ->
                array.map { it as Triple<String, Float, Float> }
            }

            val writer = file.bufferedWriter()
            writer.append("Cryptocurrency Name,Min Price,Max Price\n")

            combinedFlux
                .doOnNext { cryptoInfoList ->
                    cryptoInfoList.forEach { (name, minPrice, maxPrice) ->
                        writer.append("$name,$minPrice,$maxPrice\n")
                    }
                }
                .collectList()
                .doOnSuccess {
                    writer.close()
                }
                .thenReturn(file)
        }
    }
}
