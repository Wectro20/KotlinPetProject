package com.ajax.cryptocurrency.grpc

import com.ajax.cryptocurrency.CryptocurrencyOuterClass
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyResponse
import com.ajax.cryptocurrency.ReactorCryptocurrencyServiceGrpc
import com.ajax.cryptocurrency.application.ports.repository.CryptocurrencyRepositoryOutPort
import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import com.ajax.cryptocurrency.infrastructure.database.redis.RedisCryptocurrencyRepository
import com.ajax.cryptocurrency.infrastructure.service.CryptocurrencyService
import com.ajax.cryptocurrency.util.cryptocurrencyToProto
import com.google.protobuf.ByteString
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.time.ZoneOffset


@ExtendWith(MockKExtension::class)
class CryptocurrencyGrpcServiceTest {

    @MockK
    private lateinit var cryptocurrencyRepository: CryptocurrencyRepositoryOutPort

    private lateinit var cryptocurrencyServiceImpl: CryptocurrencyService

    private lateinit var redisCryptocurrencyRepository: RedisCryptocurrencyRepository

    private lateinit var stub: ReactorCryptocurrencyServiceGrpc.ReactorCryptocurrencyServiceStub
    private lateinit var channel: ManagedChannel
    private var grpcPort = 9090

    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: String = "63b346f12b207611fc867ff3"
    private val domainCryptocurrencyLists = listOf(
        DomainCryptocurrency(id, "BTC", 12341f, time),
        DomainCryptocurrency(id, "BTC", 23455f, time),
        DomainCryptocurrency(id, "ETH", 1200f, time),
        DomainCryptocurrency(id, "ETH", 1300f, time),
        DomainCryptocurrency(id, "ETH", 1400f, time),
        DomainCryptocurrency(id, "XRP", 300f, time),
        DomainCryptocurrency(id, "XRP", 520f, time)
    )

    private val cryptoMap = mapOf(
        "BTC" to DomainCryptocurrency(id, "BTC", 12341f, time),
        "ETH" to DomainCryptocurrency(id, "ETH", 12341f, time),
        "XRP" to DomainCryptocurrency(id, "XRP", 12341f, time)
    )

    @BeforeEach
    fun setup() {
        stub = mockk()
        redisCryptocurrencyRepository = mockk()
        channel = ManagedChannelBuilder
            .forAddress("localhost", grpcPort)
            .usePlaintext()
            .build()
        cryptocurrencyServiceImpl = CryptocurrencyService(
            cryptocurrencyRepository,
            redisCryptocurrencyRepository,
            listOf("BTC", "ETH", "XRP")
        )
    }

    @Test
    fun findAllCryptocurrenciesTest() {
        val request = CryptocurrencyRequest.newBuilder().build()

        val responseList = Mono.just(domainCryptocurrencyLists.map {
            it.cryptocurrencyToProto()
        })
            .map { allCryptocurrency ->
                CryptocurrencyResponse.newBuilder().apply {
                    cryptocurrencyList = CryptocurrencyOuterClass.CryptocurrencyList.newBuilder()
                        .addAllCryptocurrency(allCryptocurrency)
                        .build()
                }.build()
            }

        val expectedCryptocurrencyList = domainCryptocurrencyLists.map {
            it.cryptocurrencyToProto()
        }

        every { stub.findAllCryptocurrencies(request) } returns responseList

        val responseMono = stub.findAllCryptocurrencies(request)

        StepVerifier.create(responseMono)
            .expectNextMatches { response ->
                val actualList = response.cryptocurrencyList.cryptocurrencyList
                assertEquals(expectedCryptocurrencyList, actualList)
                true
            }
            .verifyComplete()
    }


    @ParameterizedTest
    @ValueSource(strings = ["BTC", "ETH", "XRP"])
    fun findMinPriceByCryptocurrencyName(cryptoName: String) {
        val request = CryptocurrencyRequest.newBuilder().apply {
            cryptocurrencyMinMaxBuilder
                .setName(cryptoName)
                .setSortOrder(1)
        }.build()

        val cryptoResponse = CryptocurrencyResponse.newBuilder()
            .setCryptocurrency(cryptoMap[cryptoName]!!.cryptocurrencyToProto())
            .build()

        every { stub.findMinMaxPriceByCryptocurrencyName(request) } returns Mono.just(cryptoResponse)

        val responseMono = stub.findMinMaxPriceByCryptocurrencyName(request)

        StepVerifier.create(responseMono)
            .expectNext(cryptoResponse)
            .verifyComplete()
    }

    @ParameterizedTest
    @ValueSource(strings = ["BTC", "ETH", "XRP"])
    fun findMaxPriceByCryptocurrencyName(cryptoName: String) {
        val request = CryptocurrencyRequest.newBuilder().apply {
            cryptocurrencyMinMaxBuilder
                .setName(cryptoName)
                .setSortOrder(-1)
        }.build()

        val cryptoResponse = CryptocurrencyResponse.newBuilder()
            .setCryptocurrency(cryptoMap[cryptoName]!!.cryptocurrencyToProto())
            .build()

        every { stub.findMinMaxPriceByCryptocurrencyName(request) } returns Mono.just(cryptoResponse)

        val responseMono = stub.findMinMaxPriceByCryptocurrencyName(request)

        StepVerifier.create(responseMono)
            .expectNext(cryptoResponse)
            .verifyComplete()
    }

    @ParameterizedTest
    @ValueSource(strings = ["BTC", "ETH", "XRP"])
    fun getCryptocurrencyPagesTest(cryptoName: String) {
        val request = CryptocurrencyRequest.newBuilder().apply {
            pageBuilder
                .setName(cryptoName)
                .setPageNumber(0)
                .setPageSize(10)
        }.build()

        val sortedList = domainCryptocurrencyLists.filter { it.cryptocurrencyName == cryptoName }

        val responseList = Mono.just(sortedList.map {
            it.cryptocurrencyToProto()
        })
            .map { allCryptocurrency ->
                CryptocurrencyResponse.newBuilder().apply {
                    cryptocurrencyList = CryptocurrencyOuterClass.CryptocurrencyList.newBuilder()
                        .addAllCryptocurrency(allCryptocurrency)
                        .build()
                }.build()
            }

        val expectedCryptocurrencyList = sortedList.map {
            it.cryptocurrencyToProto()
        }

        every { stub.getCryptocurrencyPages(request) } returns responseList

        val responseMono = stub.getCryptocurrencyPages(request)

        StepVerifier.create(responseMono)
            .expectNextMatches { response ->
                val actualList = response.cryptocurrencyList.cryptocurrencyList
                assertEquals(expectedCryptocurrencyList, actualList)
                true
            }
            .verifyComplete()
    }

    @Test
    fun writeCsvFileTest() {
        val request = CryptocurrencyRequest.newBuilder().apply {
            nameBuilder.setName("cryptocurrency-prices-test")
        }.build()

        every { cryptocurrencyRepository.findMinMaxByName("BTC", -1) } returns Mono.just(cryptoMap["BTC"]!!)
        every { cryptocurrencyRepository.findMinMaxByName("BTC", 1) } returns Mono.just(cryptoMap["BTC"]!!)
        every { cryptocurrencyRepository.findMinMaxByName("ETH", -1) } returns Mono.just(cryptoMap["ETH"]!!)
        every { cryptocurrencyRepository.findMinMaxByName("ETH", 1) } returns Mono.just(cryptoMap["ETH"]!!)
        every { cryptocurrencyRepository.findMinMaxByName("XRP", -1) } returns Mono.just(cryptoMap["XRP"]!!)
        every { cryptocurrencyRepository.findMinMaxByName("XRP", 1) } returns Mono.just(cryptoMap["XRP"]!!)

        val resultMono = cryptocurrencyServiceImpl.writeCsv("cryptocurrency-prices-test")
        val expectedResponse = resultMono.flatMap { file ->
            val fileBytes = ByteString.copyFrom(file.readBytes())

            val cryptocurrencyResponse = CryptocurrencyResponse.newBuilder()
                .setFile(CryptocurrencyOuterClass.CryptocurrencyFile.newBuilder().setFile(fileBytes))
                .build()

            Mono.just(cryptocurrencyResponse)
        }

        every { stub.writeCsvFile(request) } returns expectedResponse

        val responseMono = stub.writeCsvFile(request)

        StepVerifier.create(responseMono)
            .expectNext(expectedResponse.block())
            .verifyComplete()
    }

    @AfterEach
    fun tearDown() {
        channel.shutdown()
    }
}
