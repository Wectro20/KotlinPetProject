package com.ajax.cryptocurrency.natscontroller

import com.ajax.cryptocurrency.nats.NatsCryptocurrencyGetAllController
import com.ajax.cryptocurrency.service.CryptocurrencyService
import com.ajax.cryptocurrency.service.convertproto.CryptocurrencyConvertor
import cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import com.ajax.cryptocurrency.model.Cryptocurrency
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Assertions.assertEquals
import io.mockk.verify
import io.nats.client.Connection
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

@ExtendWith(MockKExtension::class)
class NatsCryptocurrencyGetAllControllerTest {
    @MockK
    private lateinit var cryptocurrencyService: CryptocurrencyService

    @MockK
    private lateinit var cryptocurrencyConvertor: CryptocurrencyConvertor

    @Suppress("UnusedPrivateProperty")
    @MockK
    private lateinit var connection: Connection

    @InjectMockKs
    private lateinit var controller: NatsCryptocurrencyGetAllController
    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: ObjectId = ObjectId("63b346f12b207611fc867ff3")
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


    @Test
    fun testHandler() {
        val request = CryptocurrencyRequest.newBuilder().build()

        every { cryptocurrencyService.findAll().collectList().block() } returns cryptocurrencyList

        cryptocurrencyList.forEach { crypto ->
            every {
                cryptocurrencyConvertor.cryptocurrencyToProto(crypto)
            } returns crypto.toProto()
        }

        val response = controller.handler(request)
        val cryptoListFromResponse = response.cryptocurrencyList.cryptocurrencyList.map { it.toDomain() }

        assertEquals(
            cryptocurrencyList.map { it.cryptocurrencyName },
            cryptoListFromResponse.map { it.cryptocurrencyName })
        assertEquals(cryptocurrencyList.map { it.price }, cryptoListFromResponse.map { it.price })
        assertEquals(cryptocurrencyList.map { it.createdTime }, cryptoListFromResponse.map { it.createdTime })
        verify { cryptocurrencyService.findAll().collectList().block() }

        cryptocurrencyList.forEach { crypto ->
            verify {
                cryptocurrencyConvertor.cryptocurrencyToProto(crypto)
            }
        }
    }
}
