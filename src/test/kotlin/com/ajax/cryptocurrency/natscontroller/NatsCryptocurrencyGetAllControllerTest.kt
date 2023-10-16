package com.ajax.cryptocurrency.natscontroller

import com.ajax.cryptocurrency.CryptocurrencyOuterClass
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import com.ajax.cryptocurrency.infrastructure.convertproto.CryptocurrencyConvertor
import com.ajax.cryptocurrency.config.TestConfig
import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import com.ajax.cryptocurrency.infrastructure.nats.NatsCryptocurrencyGetAllController
import com.ajax.cryptocurrency.infrastructure.service.CryptocurrencyService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.nats.client.Connection
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.time.ZoneOffset

@SpringBootTest
@ExtendWith(MockKExtension::class)
@ContextConfiguration(classes = [TestConfig::class])
class NatsCryptocurrencyGetAllControllerTest {
    @MockK
    private lateinit var cryptocurrencyServiceImpl: CryptocurrencyService

    @Suppress("UnusedPrivateProperty")
    @MockK
    private lateinit var connection: Connection

    @InjectMockKs
    private lateinit var controller: NatsCryptocurrencyGetAllController

    @Autowired
    private lateinit var cryptocurrencyConvertor: CryptocurrencyConvertor

    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: String = "63b346f12b207611fc867ff3"
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


    @Test
    fun testHandler() {
        val request = CryptocurrencyRequest.newBuilder().build()

        every { cryptocurrencyServiceImpl.findAll() } returns Flux.fromIterable(domainCryptocurrencyLists)

        val responseMono: Mono<CryptocurrencyOuterClass.CryptocurrencyResponse> = controller.handler(request)

        StepVerifier.create(responseMono)
            .assertNext { response ->
                val cryptoListFromResponse = response.cryptocurrencyList.cryptocurrencyList.map {
                    cryptocurrencyConvertor.protoToCryptocurrency(it)
                }
                assertEquals(
                    domainCryptocurrencyLists.map { it.cryptocurrencyName },
                    cryptoListFromResponse.map { it.cryptocurrencyName })
                assertEquals(
                    domainCryptocurrencyLists.map { it.price },
                    cryptoListFromResponse.map { it.price }
                )
                assertEquals(
                    domainCryptocurrencyLists.map { it.createdTime },
                    cryptoListFromResponse.map { it.createdTime }
                )
            }
            .verifyComplete()

        verify { cryptocurrencyServiceImpl.findAll() }
    }
}
