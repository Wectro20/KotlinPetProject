package com.ajax.cryptocurrency.parser

import com.ajax.cryptocurrency.annotation.ScheduledBackgroundJobStarter
import com.ajax.cryptocurrency.kafka.CryptocurrencyKafkaProducer
import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.parser.interfaces.ParserInterface
import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.time.OffsetDateTime
import java.time.ZoneOffset

@ScheduledBackgroundJobStarter(startDelay = 10000, period = 5000)
class Parser(
    private val cryptocurrencyRepository: CryptocurrencyRepository,
    private val cryptocurrencyKafkaProducer: CryptocurrencyKafkaProducer
): ParserInterface {

    override fun savePrices(cryptocurrencyName: String) {
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
                        cryptocurrencyName = cryptocurrencyName,
                        price = price,
                        createdTime = createdTimeStamp.toLocalDateTime()
                    )

                    cryptocurrencyRepository.save(cryptocurrency)
                        .doAfterTerminate{
                            cryptocurrencyKafkaProducer.sendCryptocurrencyToKafka(cryptocurrency)
                        }.subscribe()
                }
            }.onFailure { e ->
                logger.error("An exception occurred while saving prices", e)
            }
        }

    private companion object {
        private val logger: Logger = LoggerFactory.getLogger(Parser::class.java)
    }
}
