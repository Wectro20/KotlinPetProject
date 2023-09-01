package com.ajax.cryptocurrency.threads

import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Thread.currentThread
import java.lang.Thread.sleep
import java.net.URL
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Component
data class PriceSaver(
    @Autowired private val cryptocurrencyRepository: CryptocurrencyRepository,
    @Value("\${cryptocurrency.name}") private val cryptocurrencyNames: List<String>,
    @Value("\${cryptocurrency.thread-sleep-time}") private val threadSleepTime: Long
) : Runnable {

    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(PriceSaver::class.java)
    }

    override fun run() {
        logger.info("Parsing data started")
        while (true) {
            try {
                savePrices()
                sleep(threadSleepTime)
            } catch (e: InterruptedException) {
                logger.warn("Price saver thread was interrupted", e)
                currentThread().interrupt()
            }
        }
    }

    private fun savePrices() {
        for (cryptocurrencyName in cryptocurrencyNames) {
            val url = "https://cex.io/api/last_price/$cryptocurrencyName/USD"

            runCatching {
                val btcUrl = URL(url)
                val reader = BufferedReader(InputStreamReader(btcUrl.openStream()))
                reader.use { bufferedReader ->
                    val line: String = bufferedReader.readLine()
                    val jsonObject = JSONObject(line)
                    val price: Float = jsonObject.getFloat("lprice")

                    val createdTimeStamp = OffsetDateTime.now(ZoneOffset.UTC)
                    val cryptocurrency = Cryptocurrency(null, cryptocurrencyName = cryptocurrencyName, price = price, createdTime = createdTimeStamp.toLocalDateTime())

                    cryptocurrencyRepository.save(cryptocurrency)
                }
            }.onFailure { e ->
                logger.error("An exception occurred while saving prices", e)
            }
        }
    }
}