package com.ajax.cryptocurrency.service

import com.ajax.cryptocurrency.domain.CryptocurrencyDomain
import com.ajax.cryptocurrency.infrastructure.service.CryptocurrencyServiceImpl
import com.ajax.cryptocurrency.infrastructure.mongo.repository.CryptocurrencyRepositoryImpl
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.OffsetDateTime
import java.time.ZoneOffset

@ExtendWith(MockKExtension::class)
class CryptocurrencyServiceImplTest {
    @MockK
    private lateinit var cryptocurrencyRepository: CryptocurrencyRepositoryImpl

    private lateinit var cryptocurrencyServiceImpl: CryptocurrencyServiceImpl

    private val expectedCsvFile = "src/test/resources/expected.csv"
    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: ObjectId = ObjectId("63b346f12b207611fc867ff3")
    private val cryptoMap = mapOf(
        "BTC" to CryptocurrencyDomain(id, "BTC", 12341f, time),
        "ETH" to CryptocurrencyDomain(id, "ETH", 12341f, time),
        "XRP" to CryptocurrencyDomain(id, "XRP", 12341f, time)
    )

    private val cryptocurrencyDomainList = listOf(
        CryptocurrencyDomain(id, "BTC", 12341f, time),
        CryptocurrencyDomain(id, "BTC", 23455f, time),
        CryptocurrencyDomain(id, "ETH", 1200f, time),
        CryptocurrencyDomain(id, "ETH", 1300f, time),
        CryptocurrencyDomain(id, "ETH", 1400f, time),
        CryptocurrencyDomain(id, "XRP", 200f, time),
        CryptocurrencyDomain(id, "XRP", 300f, time),
        CryptocurrencyDomain(id, "XRP", 520f, time)
    )

    @BeforeEach
    fun setup() {
        cryptocurrencyServiceImpl = CryptocurrencyServiceImpl(cryptocurrencyRepository, listOf("BTC", "ETH", "XRP"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["BTC", "ETH", "XRP"])
    fun findCryptoMinPriceTest(cryptoName: String) {
        every {
            cryptocurrencyRepository.findMinMaxByName(cryptoName, 1)
        } returns Mono.just(cryptoMap[cryptoName]!!)
        val result = cryptocurrencyServiceImpl.findMinMaxPriceByCryptocurrencyName(cryptoName, 1)
        StepVerifier
            .create(result)
            .consumeNextWith { actual ->
                assertEquals(actual.id, cryptoMap[cryptoName]!!.id)
                assertEquals(actual.price, cryptoMap[cryptoName]!!.price)
                assertEquals(actual.cryptocurrencyName, cryptoMap[cryptoName]!!.cryptocurrencyName)
                assertEquals(actual.createdTime, cryptoMap[cryptoName]!!.createdTime)
            }
            .verifyComplete()
    }

    @ParameterizedTest
    @ValueSource(strings = ["BTC", "ETH", "XRP"])
    fun findCryptoMaxPriceTest(cryptoName: String) {
        every {
            cryptocurrencyRepository.findMinMaxByName(cryptoName, -1)
        } returns Mono.just(cryptoMap[cryptoName]!!)
        val result = cryptocurrencyServiceImpl.findMinMaxPriceByCryptocurrencyName(cryptoName, -1)
        StepVerifier
            .create(result)
            .consumeNextWith { actual ->
                assertEquals(actual.id, cryptoMap[cryptoName]!!.id)
                assertEquals(actual.price, cryptoMap[cryptoName]!!.price)
                assertEquals(actual.cryptocurrencyName, cryptoMap[cryptoName]!!.cryptocurrencyName)
                assertEquals(actual.createdTime, cryptoMap[cryptoName]!!.createdTime)
            }
            .verifyComplete()
    }

    @ParameterizedTest
    @ValueSource(strings = ["BTC", "ETH", "XRP"])
    fun getCryptoPageTest(cryptoName: String) {
        val sortedList = cryptocurrencyDomainList.filter { it.cryptocurrencyName == cryptoName }
            .sortedBy { it.price }
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        every {
            cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName(cryptoName, pageable)
        } returns Flux.fromIterable(sortedList)
        val result = cryptocurrencyServiceImpl.getCryptocurrencyPages(cryptoName, 0, 10)
        StepVerifier
            .create(result)
            .expectNextCount(sortedList.size.toLong())
            .verifyComplete()
    }

    @Test
    fun createCSV() {
        every { cryptocurrencyRepository.findMinMaxByName("BTC", -1) } returns Mono.just(cryptoMap["BTC"]!!)
        every { cryptocurrencyRepository.findMinMaxByName("BTC", 1) } returns Mono.just(cryptoMap["BTC"]!!)
        every { cryptocurrencyRepository.findMinMaxByName("ETH", -1) } returns Mono.just(cryptoMap["ETH"]!!)
        every { cryptocurrencyRepository.findMinMaxByName("ETH", 1) } returns Mono.just(cryptoMap["ETH"]!!)
        every { cryptocurrencyRepository.findMinMaxByName("XRP", -1) } returns Mono.just(cryptoMap["XRP"]!!)
        every { cryptocurrencyRepository.findMinMaxByName("XRP", 1) } returns Mono.just(cryptoMap["XRP"]!!)

        val resultMono = cryptocurrencyServiceImpl.writeCsv("cryptocurrency-prices-test")
        StepVerifier.create(resultMono)
            .expectNextMatches { result ->
                val actual = isEqual(Paths.get(expectedCsvFile), Paths.get(result.absolutePath))
                result.delete()
                actual
            }
            .verifyComplete()
    }

    private fun isEqual(firstFile: Path, secondFile: Path): Boolean {
        if (Files.size(firstFile) != Files.size(secondFile)) {
            return false
        }
        val first = Files.readAllBytes(firstFile)
        val second = Files.readAllBytes(secondFile)
        return first.contentEquals(second)
    }
}