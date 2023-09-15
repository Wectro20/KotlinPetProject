package com.ajax.cryptocurrency.nats

import CryptocurrencyOuterClass.CryptocurrencyResponse
import CryptocurrencyOuterClass.CryptocurrencyRequest
import CryptocurrencyOuterClass.CryptocurrencyFile
import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import com.ajax.cryptocurrency.service.CryptocurrencyService
import com.ajax.cryptocurrency.service.convertproto.CryptocurrencyConvertor
import com.google.protobuf.ByteString
import com.google.protobuf.Parser
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.nats.client.Connection
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset

@ExtendWith(MockKExtension::class)
class NatsCryptocurrencyGetCsvControllerTest {
    @MockK
    private lateinit var cryptocurrencyRepository: CryptocurrencyRepository

    private lateinit var cryptocurrencyService: CryptocurrencyService
    private lateinit var connection: Connection
    private lateinit var controller: NatsCryptocurrencyGetCsvController

    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: ObjectId = ObjectId("63b346f12b207611fc867ff3")
    private var cryptocurrencies: List<String> = listOf("BTC","ETH","XRP")
    private val expectedCsvFile = "src/test/resources/expected.csv"

    private val cryptocurrencyBTCPrice = Cryptocurrency(id, "BTC", 12341f, time)
    private val cryptocurrencyETHPrice = Cryptocurrency(id, "ETH", 12341f, time)
    private val cryptocurrencyXRPPrice = Cryptocurrency(id, "XRP", 12341f, time)

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
        connection = mockk()
        cryptocurrencyService = CryptocurrencyService(cryptocurrencyRepository, cryptocurrencies)

        controller = NatsCryptocurrencyGetCsvController(
            cryptocurrencyService,
            connection
        )

    }

//    val names = CryptocurrencyOuterClass.CryptocurrencyName.newBuilder().setName("cryptocurrency")
//
//    val a = CryptocurrencyRequest.newBuilder().setName(names).build()
//    val b = natsConnection.requestWithTimeout("v2.input.reqreply.company.cryptocurrency.get_csv", a.toByteArray(), Duration.ofMillis(10000))
//    val responseBytes = b.get().data
//    val response = CryptocurrencyOuterClass.CryptocurrencyResponse.parseFrom(responseBytes)
//    println(response)

    @Test
    fun testHandler() {
        val names = CryptocurrencyOuterClass.CryptocurrencyName.newBuilder().setName("cryptocurrency")
        val a = CryptocurrencyRequest.newBuilder().setName(names).build()
        val b = connection.request("v2.input.reqreply.company.cryptocurrency.get_csv", a.toByteArray())
        val responseBytes = b.get().data
        val response = CryptocurrencyOuterClass.CryptocurrencyResponse.parseFrom(responseBytes)
        println(response)
    }
}
