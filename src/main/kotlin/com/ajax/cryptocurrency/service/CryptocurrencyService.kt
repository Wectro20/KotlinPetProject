package com.ajax.cryptocurrency.service

import com.ajax.cryptocurrency.exception.CryptocurrencyPriceNotFoundException
import com.ajax.cryptocurrency.exception.InvalidParamsForThePageException
import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter

@Service
class CryptocurrencyService(private val cryptocurrencyRepository: CryptocurrencyRepository) {
    fun findMinMaxPriceByCryptocurrencyName(name: String, sortOrder: Int?): Cryptocurrency {
        val unwrappedSortOrder = sortOrder ?: throw IllegalArgumentException("Sort order cannot be null")

        val priceMinMax = cryptocurrencyRepository.findMinMaxByName(name, unwrappedSortOrder)
            ?: throw CryptocurrencyPriceNotFoundException("Price of $name cryptocurrency not found")

        return priceMinMax
    }

    fun findAll(): List<Cryptocurrency> {
        return cryptocurrencyRepository.findAll()
    }

    fun getCryptocurrencyPages(name: String?, pageNo: Int, pageSize: Int): MutableList<Cryptocurrency?> {
        when {
            cryptocurrencyRepository.findAll()
                .isEmpty() -> throw CryptocurrencyPriceNotFoundException("cryptocurrency prices not found")

            pageNo < 0 || pageSize < 0 -> throw InvalidParamsForThePageException("invalid params for the page")
            else -> {
                val pageable = PageRequest.of(pageNo, pageSize, Sort.by("price"))
                val pagedResult = name?.let {
                    cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName(name, pageable)
                } ?: cryptocurrencyRepository.findAll(pageable)
                return pagedResult.content
            }
        }
    }

    fun writeCsv(fileName: String): File {
        val cryptocurrencyNames = arrayOf("BTC", "ETH", "XRP")

        val file = File("./$fileName.csv")
        val writer = FileWriter(file)
        try {
            writer.append("Cryptocurrency Name,Min Price,Max Price\n")

            for (name in cryptocurrencyNames) {
                val minPrice = cryptocurrencyRepository.findMinMaxByName(name, 1)?.price.toString()
                val maxPrice = cryptocurrencyRepository.findMinMaxByName(name, -1)?.price.toString()

                writer.append("$name,$minPrice,$maxPrice\n")
            }

            writer.flush()
        } finally {
            writer.close()
        }
        return file
    }
}
