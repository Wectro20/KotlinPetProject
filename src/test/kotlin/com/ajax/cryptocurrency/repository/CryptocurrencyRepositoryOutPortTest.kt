package com.ajax.cryptocurrency.repository

import com.ajax.cryptocurrency.application.ports.repository.CryptocurrencyRepositoryOutPort
import com.ajax.cryptocurrency.domain.DomainCryptocurrency
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
class CryptocurrencyRepositoryOutPortTest {

    private lateinit var mockRepository: CryptocurrencyRepositoryOutPort

    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: String = "63b346f12b207611fc867ff3"
    private val domainCryptocurrencyBTCPrice = DomainCryptocurrency(id, "BTC", 12341f, time)

    private val domainCryptocurrencyLists = listOf(
        DomainCryptocurrency(id, "BTC", 12341f, time),
        DomainCryptocurrency(id, "BTC", 23455f, time),
        DomainCryptocurrency(id, "ETH", 1200f, time),
        DomainCryptocurrency(id, "ETH", 1300f, time),
        DomainCryptocurrency(id, "ETH", 1400f, time),
        DomainCryptocurrency(id, "XRP", 200f, time),
        DomainCryptocurrency(id, "XRP", 300f, time),
        DomainCryptocurrency(id, "XRP", 520f, time)
    )

    @BeforeEach
    fun setup() {
        mockRepository = mockk<CryptocurrencyRepositoryOutPort>()
    }

    @Test
    fun saveTest() {
        every { mockRepository.save(domainCryptocurrencyBTCPrice) } returns Mono.just(domainCryptocurrencyBTCPrice)
        val setup = mockRepository.save(domainCryptocurrencyBTCPrice)
        StepVerifier
            .create(setup)
            .expectNext(domainCryptocurrencyBTCPrice)
            .verifyComplete()
    }

    @Test
    fun findAllTest() {
        every { mockRepository.findAll() } returns Flux.fromIterable(domainCryptocurrencyLists)
        val setup = mockRepository.findAll()
        StepVerifier
            .create(setup)
            .expectNextCount(domainCryptocurrencyLists.size.toLong())
            .verifyComplete()
    }

    @Test
    fun findMinMaxByNameTest() {
        every { mockRepository.findMinMaxByName("BTC", -1) } returns Mono.just(domainCryptocurrencyBTCPrice)
        val setup = mockRepository.findMinMaxByName("BTC", -1)
        StepVerifier
            .create(setup)
            .expectNext(domainCryptocurrencyBTCPrice)
            .verifyComplete()
    }

    @Test
    fun findAllByTest() {
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        every { mockRepository.findAllBy(pageable) } returns Flux.fromIterable(domainCryptocurrencyLists)
        val setup = mockRepository.findAllBy(pageable)
        StepVerifier
            .create(setup)
            .expectNextCount(domainCryptocurrencyLists.size.toLong())
            .verifyComplete()
    }

    @ParameterizedTest
    @ValueSource(strings = ["BTC", "ETH", "XRP"])
    fun findCryptocurrencyPriceByCryptocurrencyNameTest(cryptoName: String) {
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        val sortedList = domainCryptocurrencyLists.filter { it.cryptocurrencyName == cryptoName }
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
