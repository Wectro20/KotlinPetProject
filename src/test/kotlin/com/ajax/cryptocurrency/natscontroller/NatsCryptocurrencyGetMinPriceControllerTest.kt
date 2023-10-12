package com.ajax.cryptocurrency.natscontroller

import com.ajax.cryptocurrency.CryptocurrencyOuterClass
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyName
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.nats.NatsCryptocurrencyGetMinPriceController
import com.ajax.cryptocurrency.service.CryptocurrencyService
import com.ajax.cryptocurrency.service.convertproto.CryptocurrencyConvertor
import com.ajax.cryptocurrency.toDomain
import com.ajax.cryptocurrency.toProto
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
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.time.ZoneOffset

@ExtendWith(MockKExtension::class)
class NatsCryptocurrencyGetMinPriceControllerTest {
    @MockK
    private lateinit var cryptocurrencyService: CryptocurrencyService

    @MockK
    private lateinit var cryptocurrencyConvertor: CryptocurrencyConvertor

    @Suppress("UnusedPrivateProperty")
    @MockK
    private lateinit var connection: Connection

    @InjectMockKs
    private lateinit var controller: NatsCryptocurrencyGetMinPriceController

    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: ObjectId = ObjectId("63b346f12b207611fc867ff3")

    private val cryptocurrencyMap = mapOf(
        "BTC" to Cryptocurrency(id, "BTC", 12341f, time),
        "ETH" to Cryptocurrency(id, "ETH", 12341f, time),
        "XRP" to Cryptocurrency(id, "XRP", 12341f, time)
    )

    @ParameterizedTest
    @ValueSource(strings = ["BTC", "ETH", "XRP"])
    fun testHandler(cryptoName: String) {
        val name = CryptocurrencyName.newBuilder().setName(cryptoName).build()
        val request = CryptocurrencyRequest.newBuilder().setName(name).build()

        val crypto = cryptocurrencyMap[cryptoName]!!

        every {
            cryptocurrencyService.findMinMaxPriceByCryptocurrencyName(cryptoName, 1)
        } returns Mono.just(crypto) // Returning a Mono instead of blocking response

        every {
            cryptocurrencyConvertor.cryptocurrencyToProto(crypto)
        } returns crypto.toProto()

        val responseMono: Mono<CryptocurrencyOuterClass.CryptocurrencyResponse> = controller.handler(request)

        StepVerifier.create(responseMono)
            .assertNext { response ->
                val cryptoFromResponse = response.cryptocurrency.toDomain()

                assertEquals(crypto.cryptocurrencyName, cryptoFromResponse.cryptocurrencyName)
                assertEquals(crypto.price, cryptoFromResponse.price)
                assertEquals(crypto.createdTime, cryptoFromResponse.createdTime)
            }
            .verifyComplete()

        verify { cryptocurrencyService.findMinMaxPriceByCryptocurrencyName(cryptoName, 1) }
        verify { cryptocurrencyConvertor.cryptocurrencyToProto(crypto) }
    }
}
