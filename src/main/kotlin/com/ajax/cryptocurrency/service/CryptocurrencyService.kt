package com.ajax.cryptocurrency.service

import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.io.File

@Service
class CryptocurrencyService(
    private val cryptocurrencyRepository: CryptocurrencyRepository,
    @Value("\${cryptocurrency.name}") private val cryptocurrencies: List<String>,

) {
    fun findMinMaxPriceByCryptocurrencyName(name: String, sortOrder: Int): Cryptocurrency =
        cryptocurrencyRepository.findMinMaxByName(name, sortOrder)

    fun findAll(): List<Cryptocurrency> {
        return cryptocurrencyRepository.findAll()
    }

    fun getCryptocurrencyPages(name: String?, pageNumber: Int, pageSize: Int): List<Cryptocurrency> {
        val pageable = PageRequest.of(pageNumber, pageSize, Sort.by("price"))
        return if (name == null)
            cryptocurrencyRepository.findAll(pageable).content
        else {
            cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName(name, pageable).content
        }
    }

    fun writeCsv(fileName: String): File {
        val file = File("./$fileName.csv")

        file.bufferedWriter().use { writer ->
            writer.append("Cryptocurrency Name,Min Price,Max Price\n")

            for (name in cryptocurrencies) {
                val minPrice = cryptocurrencyRepository.findMinMaxByName(name, 1).price
                val maxPrice = cryptocurrencyRepository.findMinMaxByName(name, -1).price

                writer.append("$name,$minPrice,$maxPrice\n")
            }
        }

        return file
    }
}
