package com.ajax.cryptocurrency.natscontroller

import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.nats.NatsCryptocurrencyPagesController
import com.ajax.cryptocurrency.service.CryptocurrencyService
import com.ajax.cryptocurrency.service.convertproto.CryptocurrencyConvertor
import cryptocurrency.CryptocurrencyOuterClass
import cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import io.nats.client.Connection
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import reactor.core.publisher.Flux
import java.time.OffsetDateTime
import java.time.ZoneOffset

@ExtendWith(MockKExtension::class)
class NatsCryptocurrencyPagesControllerTest {
    @MockK
    private lateinit var cryptocurrencyService: CryptocurrencyService

    @MockK
    private lateinit var cryptocurrencyConvertor: CryptocurrencyConvertor

    @Suppress("UnusedPrivateProperty")
    @MockK
    private lateinit var connection: Connection

    @InjectMockKs
    private lateinit var controller: NatsCryptocurrencyPagesController

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

    @BeforeEach
    fun setUp() {
        cryptocurrencyService = mockk()
        cryptocurrencyConvertor = mockk()
        connection = mockk()

        controller = NatsCryptocurrencyPagesController(
            cryptocurrencyService,
            cryptocurrencyConvertor,
            connection
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["BTC", "ETH", "XRP"])
    fun testHandler(cryptoName: String) {
        val sortedList = cryptocurrencyList.filter { it.cryptocurrencyName == cryptoName }

        val request = CryptocurrencyRequest.newBuilder()
            .setPage(
                CryptocurrencyOuterClass.CryptocurrencyPage.newBuilder()
                    .setName(cryptoName)
                    .setPageNumber(1)
                    .setPageSize(10)
            )
            .build()

        every {
            cryptocurrencyService.getCryptocurrencyPages(cryptoName, 1, 10)
        } returns Flux.fromIterable(sortedList)

        sortedList.forEach { crypto ->
            every {
                cryptocurrencyConvertor.cryptocurrencyToProto(crypto)
            } returns crypto.toProto()
        }

        val response = controller.handler(request)
        val cryptoListFromResponse =
            response.cryptocurrencyList.cryptocurrencyList.map { it.toDomain() }

        assertEquals(sortedList.map { it.cryptocurrencyName }, cryptoListFromResponse.map { it.cryptocurrencyName })
        assertEquals(sortedList.map { it.price }, cryptoListFromResponse.map { it.price })
        assertEquals(sortedList.map { it.createdTime }, cryptoListFromResponse.map { it.createdTime })

        verify {
            cryptocurrencyService.getCryptocurrencyPages(cryptoName, 1, 10)
        }

        sortedList.forEach { crypto ->
            verify {
                cryptocurrencyConvertor.cryptocurrencyToProto(crypto)
            }
        }
    }
}
