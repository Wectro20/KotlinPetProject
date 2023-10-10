package com.ajax.cryptocurrency.natscontroller

import com.ajax.cryptocurrency.CryptocurrencyOuterClass
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import com.ajax.cryptocurrency.application.convertproto.CryptocurrencyConvertor
import com.ajax.cryptocurrency.domain.CryptocurrencyDomain
import com.ajax.cryptocurrency.config.TestConfig
import com.ajax.cryptocurrency.infrastructure.nats.NatsCryptocurrencyPagesController
import com.ajax.cryptocurrency.infrastructure.service.CryptocurrencyServiceImpl
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.time.ZoneOffset

@SpringBootTest
@ExtendWith(MockKExtension::class)
@ContextConfiguration(classes = [TestConfig::class])
class NatsCryptocurrencyPagesControllerTest {
    @MockK
    private lateinit var cryptocurrencyServiceImpl: CryptocurrencyServiceImpl

    @Autowired
    private lateinit var cryptocurrencyConvertor: CryptocurrencyConvertor

    @Suppress("UnusedPrivateProperty")
    @MockK
    private lateinit var connection: Connection

    @InjectMockKs
    private lateinit var controller: NatsCryptocurrencyPagesController

    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: ObjectId = ObjectId("63b346f12b207611fc867ff3")

    private val cryptocurrencyDomainList = listOf(
        CryptocurrencyDomain(id, "BTC", 12341f, time),
        CryptocurrencyDomain(id, "BTC", 23455f, time),
        CryptocurrencyDomain(id, "ETH", 1200f, time),
        CryptocurrencyDomain(id, "ETH", 1300f, time),
        CryptocurrencyDomain(id, "ETH", 1400f, time),
        CryptocurrencyDomain(id, "XRP", 200f, time),
        CryptocurrencyDomain(id, "XRP", 300f, time),
        CryptocurrencyDomain(id, "XRP", 520f, time)
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
        val sortedList = cryptocurrencyDomainList.filter { it.cryptocurrencyName == cryptoName }

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

        val responseMono: Mono<CryptocurrencyOuterClass.CryptocurrencyResponse> = controller.handler(request)

        StepVerifier.create(responseMono)
            .assertNext { response ->
                val cryptoListFromResponse =
                    response.cryptocurrencyList.cryptocurrencyList.map {
                        cryptocurrencyConvertor.protoToCryptocurrency(
                            it
                        )
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
    }
}
