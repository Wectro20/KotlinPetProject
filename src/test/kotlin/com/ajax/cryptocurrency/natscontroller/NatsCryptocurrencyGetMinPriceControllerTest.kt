package com.ajax.cryptocurrency.natscontroller

import cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyName
import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.nats.NatsCryptocurrencyGetMinPriceController
import com.ajax.cryptocurrency.natscontroller.toDomain
import com.ajax.cryptocurrency.natscontroller.toProto
import com.ajax.cryptocurrency.service.CryptocurrencyService
import com.ajax.cryptocurrency.service.convertproto.CryptocurrencyConvertor
import io.nats.client.Connection
import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.OffsetDateTime
import java.time.ZoneOffset
import io.mockk.mockk
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@ExtendWith(MockKExtension::class)
class NatsCryptocurrencyGetMinPriceControllerTest {
    private lateinit var cryptocurrencyService: CryptocurrencyService
    private lateinit var cryptocurrencyConvertor: CryptocurrencyConvertor
    private lateinit var connection: Connection
    private lateinit var controller: NatsCryptocurrencyGetMinPriceController

    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: ObjectId = ObjectId("63b346f12b207611fc867ff3")

    private val cryptocurrencyMap = mapOf(
        "BTC" to Cryptocurrency(id, "BTC", 12341f, time),
        "ETH" to Cryptocurrency(id, "ETH", 12341f, time),
        "XRP" to Cryptocurrency(id, "XRP", 12341f, time)
    )

    @BeforeEach
    fun setUp() {
        cryptocurrencyService = mockk()
        cryptocurrencyConvertor = mockk()
        connection = mockk()

        controller = NatsCryptocurrencyGetMinPriceController(
            cryptocurrencyService,
            cryptocurrencyConvertor,
            connection
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["BTC", "ETH", "XRP"])
    fun testHandler(cryptoName: String) {
        val name = CryptocurrencyName.newBuilder().setName(cryptoName).build()
        val request = CryptocurrencyRequest.newBuilder().setName(name).build()

        val crypto = cryptocurrencyMap[cryptoName]!!

        every {
            cryptocurrencyService.findMinMaxPriceByCryptocurrencyName(cryptoName, 1)
        } returns crypto

        every {
            cryptocurrencyConvertor.cryptocurrencyToProto(crypto)
        } returns crypto.toProto()

        val response = controller.handler(request)

        val cryptoFromResponse = response.cryptocurrency.toDomain()

        assertEquals(crypto.cryptocurrencyName, cryptoFromResponse.cryptocurrencyName)
        assertEquals(crypto.price, cryptoFromResponse.price)
        assertEquals(crypto.createdTime, cryptoFromResponse.createdTime)

        verify { cryptocurrencyService.findMinMaxPriceByCryptocurrencyName(cryptoName, 1) }
        verify { cryptocurrencyConvertor.cryptocurrencyToProto(crypto) }
    }
}
