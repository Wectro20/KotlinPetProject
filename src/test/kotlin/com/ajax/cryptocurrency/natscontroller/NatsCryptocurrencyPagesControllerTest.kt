package com.ajax.cryptocurrency.natscontroller

import com.ajax.cryptocurrency.nats.NatsCryptocurrencyPagesController
import com.ajax.cryptocurrency.service.CryptocurrencyService
import com.ajax.cryptocurrency.service.convertproto.CryptocurrencyConvertor
import CryptocurrencyOuterClass.CryptocurrencyRequest
import com.ajax.cryptocurrency.model.Cryptocurrency
import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Assertions.assertEquals
import io.mockk.mockk
import io.mockk.verify
import io.nats.client.Connection
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

@ExtendWith(MockKExtension::class)
class NatsCryptocurrencyPagesControllerTest {
    private lateinit var cryptocurrencyService: CryptocurrencyService
    private lateinit var cryptocurrencyConvertor: CryptocurrencyConvertor
    private lateinit var connection: Connection
    private lateinit var controller: NatsCryptocurrencyPagesController

    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: ObjectId = ObjectId("63b346f12b207611fc867ff3")
    private val cryptocurrencies: List<String> = listOf("BTC", "ETH", "XRP")

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

    @Test
    fun testHandler() {
        for (cryptoName in cryptocurrencies) {
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
            } returns sortedList

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
}
