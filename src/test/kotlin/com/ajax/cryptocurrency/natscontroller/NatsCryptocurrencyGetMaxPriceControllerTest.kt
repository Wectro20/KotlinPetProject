package com.ajax.cryptocurrency.natscontroller

import com.ajax.cryptocurrency.CryptocurrencyOuterClass
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyName
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import com.ajax.cryptocurrency.infrastructure.convertproto.CryptocurrencyConvertor
import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import com.ajax.cryptocurrency.config.TestConfig
import com.ajax.cryptocurrency.infrastructure.nats.NatsCryptocurrencyGetMaxPriceController
import com.ajax.cryptocurrency.infrastructure.service.CryptocurrencyService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.nats.client.Connection
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.time.ZoneOffset

@SpringBootTest
@ExtendWith(MockKExtension::class)
@ContextConfiguration(classes = [TestConfig::class])
class NatsCryptocurrencyGetMaxPriceControllerTest {
    @MockK
    private lateinit var cryptocurrencyServiceImpl: CryptocurrencyService

    @Autowired
    private lateinit var cryptocurrencyConvertor: CryptocurrencyConvertor

    @Suppress("UnusedPrivateProperty")
    @MockK
    private lateinit var connection: Connection

    @InjectMockKs
    private lateinit var controller: NatsCryptocurrencyGetMaxPriceController

    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: String = "63b346f12b207611fc867ff3"

    private val domainCryptocurrencyMap = mapOf(
        "BTC" to DomainCryptocurrency(id, "BTC", 12341f, time),
        "ETH" to DomainCryptocurrency(id, "ETH", 12341f, time),
        "XRP" to DomainCryptocurrency(id, "XRP", 12341f, time)
    )

    @ParameterizedTest
    @ValueSource(strings = ["BTC", "ETH", "XRP"])
    fun testHandler(cryptoName: String) {
        val name = CryptocurrencyName.newBuilder().setName(cryptoName).build()
        val request = CryptocurrencyRequest.newBuilder().setName(name).build()

        val crypto = domainCryptocurrencyMap[cryptoName]!!

        every {
            cryptocurrencyServiceImpl.findMinMaxPriceByCryptocurrencyName(cryptoName, -1)
        } returns Mono.just(crypto)

        val responseMono: Mono<CryptocurrencyOuterClass.CryptocurrencyResponse> = controller.handler(request)

        StepVerifier.create(responseMono)
            .assertNext { response ->
                val cryptoFromResponse = cryptocurrencyConvertor.protoToCryptocurrency(response.cryptocurrency)

                assertEquals(crypto.cryptocurrencyName, cryptoFromResponse.cryptocurrencyName)
                assertEquals(crypto.price, cryptoFromResponse.price)
                assertEquals(crypto.createdTime, cryptoFromResponse.createdTime)
            }
            .verifyComplete()

        verify { cryptocurrencyServiceImpl.findMinMaxPriceByCryptocurrencyName(cryptoName, -1) }
    }
}
