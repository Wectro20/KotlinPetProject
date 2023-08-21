package com.ajax.cryptocurrency.service

import com.ajax.cryptocurrency.exception.CryptocurrencyPriceNotFoundException
import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.io.File

@Service
class CryptocurrencyService(private val cryptocurrencyRepository: CryptocurrencyRepository) {
    fun findMinMaxPriceByCryptocurrencyName(name: String, sortOrder: Int): Cryptocurrency {
        return cryptocurrencyRepository.findMinMaxByName(name, sortOrder)
            ?: throw CryptocurrencyPriceNotFoundException("Price of $name cryptocurrency not found")
    }

    fun findAll(): List<Cryptocurrency> {
        return cryptocurrencyRepository.findAll()
    }

    fun getCryptocurrencyPages(name: String?, pageNumber: Int, pageSize: Int): MutableList<Cryptocurrency?> {
        if (cryptocurrencyRepository.findAll().isEmpty()) {
            throw CryptocurrencyPriceNotFoundException("cryptocurrency prices not found")
        }

        val pageable = PageRequest.of(pageNumber, pageSize, Sort.by("price"))
        val pagedResult = if (name == null) {
            cryptocurrencyRepository.findAll(pageable)
        } else {
            cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName(name, pageable)
        }
        return pagedResult.content
    }

    fun writeCsv(fileName: String): File {
        val cryptocurrencyNames = arrayOf("BTC", "ETH", "XRP")

        val file = File("./$fileName.csv")

        file.bufferedWriter().use { writer ->
            writer.append("Cryptocurrency Name,Min Price,Max Price\n")

            for (name in cryptocurrencyNames) {
                val minPrice = cryptocurrencyRepository.findMinMaxByName(name, 1)?.price
                val maxPrice = cryptocurrencyRepository.findMinMaxByName(name, -1)?.price

                writer.append("$name,$minPrice,$maxPrice\n")
            }
        }

        return file
    }
}
