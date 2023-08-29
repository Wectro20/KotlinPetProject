package com.ajax.cryptocurrency.threads

import com.ajax.cryptocurrency.config.SingleShotBackgroundJob
import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.concurrent.ExecutorService

@SingleShotBackgroundJob(startDelay = 1000, maxParallelThreads = 1)
data class PriceSaver(
    private val cryptocurrencyRepository: CryptocurrencyRepository,
) : Runnable {

    lateinit var cryptocurrencyName: String
    var interval: Long = 1000
    lateinit var executor: ExecutorService


    private val task: Runnable = object : Runnable {
        override fun run() {
            val url = "https://cex.io/api/last_price/$cryptocurrencyName/USD"

            runCatching {
                val btcUrl = URL(url)
                val reader = BufferedReader(InputStreamReader(btcUrl.openStream()))
                reader.use { bufferedReader ->
                    val line: String = bufferedReader.readLine()
                    val jsonObject = JSONObject(line)
                    val price: Float = jsonObject.getFloat("lprice")

                    val createdTimeStamp = OffsetDateTime.now(ZoneOffset.UTC)
                    val cryptocurrency = Cryptocurrency(
                        null,
                        cryptocurrencyName = cryptocurrencyName,
                        price = price,
                        createdTime = createdTimeStamp.toLocalDateTime()
                    )

                    cryptocurrencyRepository.save(cryptocurrency)
                }
            }.onFailure { e ->
                logger.error("An exception occurred while saving prices", e)
            }
            Thread.sleep(interval)
            executor.submit{this.run()}
        }
    }

    override fun run() {
        println("Started parsing: $cryptocurrencyName")
        task.run()
    }

    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(PriceSaver::class.java)
    }
}
