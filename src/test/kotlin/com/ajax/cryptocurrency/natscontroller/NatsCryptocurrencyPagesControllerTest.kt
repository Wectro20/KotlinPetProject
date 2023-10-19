package com.ajax.cryptocurrency.natscontroller

import com.ajax.cryptocurrency.CryptocurrencyOuterClass
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import com.ajax.cryptocurrency.infrastructure.convertproto.CryptocurrencyConvertor
import com.ajax.cryptocurrency.infrastructure.nats.NatsCryptocurrencyPagesController
import com.ajax.cryptocurrency.infrastructure.service.CryptocurrencyService
import com.ajax.cryptocurrency.util.cryptocurrencyToProto
import com.ajax.cryptocurrency.util.protoToCryptocurrency
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import io.nats.client.Connection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.time.ZoneOffset

@ExtendWith(MockKExtension::class)
class NatsCryptocurrencyPagesControllerTest {
    @MockK
    private lateinit var cryptocurrencyServiceImpl: CryptocurrencyService

    @MockK
    private lateinit var cryptocurrencyConvertor: CryptocurrencyConvertor

    @Suppress("UnusedPrivateProperty")
    @MockK
    private lateinit var connection: Connection

    @InjectMockKs
    private lateinit var controller: NatsCryptocurrencyPagesController

    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: String = "63b346f12b207611fc867ff3"

    private val domainCryptocurrencyLists = listOf(
        DomainCryptocurrency(id, "BTC", 12341f, time),
        DomainCryptocurrency(id, "BTC", 23455f, time),
        DomainCryptocurrency(id, "ETH", 1200f, time),
        DomainCryptocurrency(id, "ETH", 1300f, time),
        DomainCryptocurrency(id, "ETH", 1400f, time),
        DomainCryptocurrency(id, "XRP", 200f, time),
        DomainCryptocurrency(id, "XRP", 300f, time),
        DomainCryptocurrency(id, "XRP", 520f, time)
    )

    @BeforeEach
    fun setUp() {
        cryptocurrencyServiceImpl = mockk()
        connection = mockk()

        controller = NatsCryptocurrencyPagesController(
            cryptocurrencyServiceImpl,
            cryptocurrencyConvertor,
            connection
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["BTC", "ETH", "XRP"])
    fun testHandler(cryptoName: String) {
        val sortedList = domainCryptocurrencyLists.filter { it.cryptocurrencyName == cryptoName }

        val request = CryptocurrencyRequest.newBuilder()
            .setPage(
                CryptocurrencyOuterClass.CryptocurrencyPage.newBuilder()
                    .setName(cryptoName)
                    .setPageNumber(1)
                    .setPageSize(10)
            )
            .build()

        every {
            cryptocurrencyServiceImpl.getCryptocurrencyPages(cryptoName, 1, 10)
        } returns Flux.fromIterable(sortedList)

        sortedList.forEach {
            every { cryptocurrencyConvertor.cryptocurrencyToProto(it) } returns it.cryptocurrencyToProto()
        }

        val responseMono: Mono<CryptocurrencyOuterClass.CryptocurrencyResponse> = controller.handler(request)

        StepVerifier.create(responseMono)
            .assertNext { response ->
                val cryptoListFromResponse =
                    response.cryptocurrencyList.cryptocurrencyList.map {
                        it.protoToCryptocurrency()
                    }

                assertEquals(sortedList.map { it.cryptocurrencyName },
                    cryptoListFromResponse.map { it.cryptocurrencyName })
                assertEquals(sortedList.map { it.price }, cryptoListFromResponse.map { it.price })
                assertEquals(sortedList.map { it.createdTime }, cryptoListFromResponse.map { it.createdTime })
            }
            .verifyComplete()

        verify {
            cryptocurrencyServiceImpl.getCryptocurrencyPages(cryptoName, 1, 10)
        }

        verify {
            sortedList.forEach {
                cryptocurrencyConvertor.cryptocurrencyToProto(it)
            }
        }
    }
}
