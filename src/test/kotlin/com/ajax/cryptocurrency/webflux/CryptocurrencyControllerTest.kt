package com.ajax.cryptocurrency.webflux;

import com.ajax.cryptocurrency.CryptocurrencyApplication
import com.ajax.cryptocurrency.domain.CryptocurrencyDomain
import com.ajax.cryptocurrency.infrastructure.mongo.entity.CryptocurrencyEntity
import com.ajax.cryptocurrency.infrastructure.service.CryptocurrencyServiceImpl
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneOffset


@ContextConfiguration(classes = [CryptocurrencyApplication::class])
@WebFluxTest
class CryptocurrencyControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient;

    @MockBean
    private lateinit var cryptocurrencyServiceImpl: CryptocurrencyServiceImpl

    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val expectedCsvFile = "src/test/resources/expected.csv"
    private val id: ObjectId = ObjectId("63b346f12b207611fc867ff3")

    private val list = listOf(
        CryptocurrencyDomain(id, "BTC", 12341f, time),
        CryptocurrencyDomain(id, "BTC", 23455f, time),
        CryptocurrencyDomain(id, "ETH", 1200f, time),
        CryptocurrencyDomain(id, "ETH", 1300f, time),
        CryptocurrencyDomain(id, "ETH", 1400f, time),
        CryptocurrencyDomain(id, "XRP", 200f, time),
        CryptocurrencyDomain(id, "XRP", 300f, time),
        CryptocurrencyDomain(id, "XRP", 520f, time)
    )

    @ParameterizedTest
    @ValueSource(strings = ["BTC", "ETH", "XRP"])
    fun getPriceTests(crypto: String) {
        val cryptocurrencyEntityPrice = CryptocurrencyEntity(id, crypto, 12341f, time)
        val cryptoMono: Mono<CryptocurrencyEntity> = Mono.just(cryptocurrencyEntityPrice)

        doReturn(cryptoMono).`when`(cryptocurrencyServiceImpl)
            .findMinMaxPriceByCryptocurrencyName(crypto, 1)
        doReturn(cryptoMono).`when`(cryptocurrencyServiceImpl)
            .findMinMaxPriceByCryptocurrencyName(crypto, -1)

        webTestClient.get()
            .uri("/cryptocurrencies/minprice?name=$crypto")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(cryptocurrencyEntityPrice.javaClass)

        webTestClient.get()
            .uri("/cryptocurrencies/maxprice?name=$crypto")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(cryptocurrencyEntityPrice.javaClass)
    }

    @ParameterizedTest
    @ValueSource(strings = ["BTC", "ETH", "XRP"])
    fun getPageTests(crypto: String) {
        val sortedList = list.filter { it.cryptocurrencyName == crypto }
            .sortedBy { it.price }

        doReturn(Flux.fromIterable(sortedList)).`when`(cryptocurrencyServiceImpl)
            .getCryptocurrencyPages(crypto, 0, 10)

        webTestClient.get()
            .uri("/cryptocurrencies?name=$crypto")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(sortedList.javaClass)
    }

    @Test
    fun downloadFileTest() {
        val fileName = "test-report"
        val file = File(expectedCsvFile)

        val fileMono: Mono<File> = Mono.just(file)
        `when`(cryptocurrencyServiceImpl.writeCsv(fileName)).thenReturn(fileMono)

        webTestClient.get()
            .uri("/cryptocurrencies/csv?fileName=$fileName")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName.csv")
            .expectHeader().contentType(MediaType.parseMediaType("text/csv"))
            .expectBody()
            .consumeWith { response ->
                val contentLength = response.responseHeaders.contentLength
                assertEquals(file.length(), contentLength)
            }

        verify(cryptocurrencyServiceImpl, times(1)).writeCsv(fileName)
    }
}
