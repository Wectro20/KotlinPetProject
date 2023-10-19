package com.ajax.cryptocurrency.natscontroller

import com.ajax.cryptocurrency.CryptocurrencyOuterClass
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyName
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import com.ajax.cryptocurrency.infrastructure.convertproto.CryptocurrencyConvertor
import com.ajax.cryptocurrency.infrastructure.nats.NatsCryptocurrencyGetMinPriceController
import com.ajax.cryptocurrency.infrastructure.service.CryptocurrencyService
import com.ajax.cryptocurrency.util.cryptocurrencyToProto
import com.ajax.cryptocurrency.util.protoToCryptocurrency
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.nats.client.Connection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.time.ZoneOffset

@ExtendWith(MockKExtension::class)
class NatsCryptocurrencyGetMinPriceControllerTest {
    @MockK
    private lateinit var cryptocurrencyServiceImpl: CryptocurrencyService

    @MockK
    private lateinit var cryptocurrencyConvertor: CryptocurrencyConvertor

    @Suppress("UnusedPrivateProperty")
    @MockK
    private lateinit var connection: Connection

    @InjectMockKs
    private lateinit var controller: NatsCryptocurrencyGetMinPriceController

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
            cryptocurrencyServiceImpl.findMinMaxPriceByCryptocurrencyName(cryptoName, 1)
        } returns Mono.just(crypto)

        every { cryptocurrencyConvertor.cryptocurrencyToProto(crypto) } returns crypto.cryptocurrencyToProto()

        val responseMono: Mono<CryptocurrencyOuterClass.CryptocurrencyResponse> = controller.handler(request)

        StepVerifier.create(responseMono)
            .assertNext { response ->
                val cryptoFromResponse = response.cryptocurrency.protoToCryptocurrency()

                assertEquals(crypto.cryptocurrencyName, cryptoFromResponse.cryptocurrencyName)
                assertEquals(crypto.price, cryptoFromResponse.price)
                assertEquals(crypto.createdTime, cryptoFromResponse.createdTime)
            }
            .verifyComplete()

        verify { cryptocurrencyServiceImpl.findMinMaxPriceByCryptocurrencyName(cryptoName, 1) }

        verify { cryptocurrencyConvertor.cryptocurrencyToProto(crypto) }
    }
}
