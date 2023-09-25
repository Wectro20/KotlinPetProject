package com.ajax.cryptocurrency.repository

import com.ajax.cryptocurrency.model.Cryptocurrency
import io.mockk.every
import io.mockk.mockk
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.time.ZoneOffset

@DataMongoTest
@ExtendWith(SpringExtension::class)
@ComponentScan("com/ajax/cryptocurrency/repository/impl")
class CryptocurrencyRepositoryTest {

    private lateinit var mockRepository: CryptocurrencyRepository

    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: ObjectId = ObjectId("63b346f12b207611fc867ff3")
    private val cryptocurrencyBTCPrice = Cryptocurrency(id, "BTC", 12341f, time)

    private val cryptocurrencyList = listOf(
        Cryptocurrency(id, "BTC", 12341f, time),
        Cryptocurrency(id, "BTC", 23455f, time),
        Cryptocurrency(id, "ETH", 1200f, time),
        Cryptocurrency(id, "ETH", 1300f, time),
        Cryptocurrency(id, "ETH", 1400f, time),
        Cryptocurrency(id, "XRP", 200f, time),
        Cryptocurrency(id, "XRP", 300f, time),
        Cryptocurrency(id, "XRP", 520f, time)
    )

    @BeforeEach
    fun setup() {
        mockRepository = mockk<CryptocurrencyRepository>()
    }

    @Test
    fun saveTest() {
        every { mockRepository.save(cryptocurrencyBTCPrice) } returns Mono.just(cryptocurrencyBTCPrice)
        val setup = mockRepository.save(cryptocurrencyBTCPrice)
        StepVerifier
            .create(setup)
            .expectNext(cryptocurrencyBTCPrice)
            .verifyComplete()
    }

    @Test
    fun findAllTest() {
        every { mockRepository.findAll() } returns Flux.fromIterable(cryptocurrencyList)
        val setup = mockRepository.findAll()
        StepVerifier
            .create(setup)
            .expectNextCount(cryptocurrencyList.size.toLong())
            .verifyComplete()
    }

    @Test
    fun findMinMaxByNameTest() {
        every { mockRepository.findMinMaxByName("BTC", -1) } returns Mono.just(cryptocurrencyBTCPrice)
        val setup = mockRepository.findMinMaxByName("BTC", -1)
        StepVerifier
            .create(setup)
            .expectNext(cryptocurrencyBTCPrice)
            .verifyComplete()
    }

    @Test
    fun findAllByTest() {
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        every { mockRepository.findAllBy(pageable) } returns Flux.fromIterable(cryptocurrencyList)
        val setup = mockRepository.findAllBy(pageable)
        StepVerifier
            .create(setup)
            .expectNextCount(cryptocurrencyList.size.toLong())
            .verifyComplete()
    }

    @ParameterizedTest
    @ValueSource(strings = ["BTC", "ETH", "XRP"])
    fun findCryptocurrencyPriceByCryptocurrencyNameTest(cryptoName: String) {
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        val sortedList = cryptocurrencyList.filter { it.cryptocurrencyName == cryptoName }
            .sortedBy { it.price }
        every { mockRepository.findCryptocurrencyPriceByCryptocurrencyName(cryptoName, pageable)
        } returns Flux.fromIterable(sortedList)
        val setup = mockRepository.findCryptocurrencyPriceByCryptocurrencyName(cryptoName, pageable)
        StepVerifier
            .create(setup)
            .expectNextCount(sortedList.size.toLong())
            .verifyComplete()
    }
}
