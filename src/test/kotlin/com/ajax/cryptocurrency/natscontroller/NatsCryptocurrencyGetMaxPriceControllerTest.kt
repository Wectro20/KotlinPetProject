package com.ajax.cryptocurrency.nats

import CryptocurrencyOuterClass.CryptocurrencyRequest
import CryptocurrencyOuterClass.CryptocurrencyName
import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.natscontroller.toDomain
import com.ajax.cryptocurrency.natscontroller.toProto
import com.ajax.cryptocurrency.service.CryptocurrencyService
import com.ajax.cryptocurrency.service.convertproto.CryptocurrencyConvertor
import io.nats.client.Connection
import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.OffsetDateTime
import java.time.ZoneOffset
import io.mockk.mockk
import io.mockk.verify
import org.bson.types.ObjectId

@ExtendWith(MockKExtension::class)
class NatsCryptocurrencyGetMaxPriceControllerTest {
    private lateinit var cryptocurrencyService: CryptocurrencyService
    private lateinit var cryptocurrencyConvertor: CryptocurrencyConvertor
    private lateinit var connection: Connection
    private lateinit var controller: NatsCryptocurrencyGetMaxPriceController

    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: ObjectId = ObjectId("63b346f12b207611fc867ff3")

    private val cryptocurrencyList = listOf(
        Cryptocurrency(id, "BTC", 12341f, time),
        Cryptocurrency(id, "ETH", 12341f, time),
        Cryptocurrency(id, "XRP", 12341f, time)
    )

    private val cryptocurrencies = listOf("BTC", "ETH", "XRP")

    @BeforeEach
    fun setUp() {
        cryptocurrencyService = mockk()
        cryptocurrencyConvertor = mockk()
        connection = mockk()

        controller = NatsCryptocurrencyGetMaxPriceController(
            cryptocurrencyService,
            cryptocurrencyConvertor,
            connection
        )
    }

    @Test
    fun testHandler() {
        for (index in 0 until cryptocurrencies.size) {
            val cryptoName = cryptocurrencies[index]
            val name = CryptocurrencyName.newBuilder().setName(cryptoName).build()
            val request = CryptocurrencyRequest.newBuilder().setName(name).build()

            val crypto = cryptocurrencyList[index]

            every {
                cryptocurrencyService.findMinMaxPriceByCryptocurrencyName(cryptoName, -1)
            } returns crypto

            every {
                cryptocurrencyConvertor.cryptocurrencyToProto(crypto)
            } returns crypto.toProto()

            val response = controller.handler(request)

            val cryptoFromResponse = response.cryptocurrency.toDomain()

            assertEquals(crypto.cryptocurrencyName, cryptoFromResponse.cryptocurrencyName)
            assertEquals(crypto.price, cryptoFromResponse.price)
            assertEquals(crypto.createdTime, cryptoFromResponse.createdTime)

            verify { cryptocurrencyService.findMinMaxPriceByCryptocurrencyName(cryptoName, -1) }
            verify { cryptocurrencyConvertor.cryptocurrencyToProto(crypto) }
        }
    }
}
