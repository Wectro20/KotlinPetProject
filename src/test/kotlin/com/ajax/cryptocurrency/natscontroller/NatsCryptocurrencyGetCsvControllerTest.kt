package com.ajax.cryptocurrency.natscontroller

import com.ajax.cryptocurrency.CryptocurrencyOuterClass
import com.ajax.cryptocurrency.infrastructure.nats.NatsCryptocurrencyGetCsvController
import com.ajax.cryptocurrency.infrastructure.service.CryptocurrencyServiceImpl
import com.google.protobuf.ByteString
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.nats.client.Connection
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.io.File

@ExtendWith(MockKExtension::class)
class NatsCryptocurrencyGetCsvControllerTest {
    @MockK
    private lateinit var cryptocurrencyServiceImpl: CryptocurrencyServiceImpl

    @Suppress("UnusedPrivateProperty")
    @MockK
    private lateinit var connection: Connection

    private lateinit var controller: NatsCryptocurrencyGetCsvController

    @BeforeEach
    fun setUp() {
        controller = NatsCryptocurrencyGetCsvController(cryptocurrencyServiceImpl, connection)
    }

    @Test
    fun testHandler() {
        val fileName = "cryptocurrency-report"
        val request = CryptocurrencyOuterClass.CryptocurrencyRequest.newBuilder()
            .setName(CryptocurrencyOuterClass.CryptocurrencyName.newBuilder().setName(fileName).build()).build()

        val mockFile = File("src/test/resources/expected.csv")
        every {
            cryptocurrencyServiceImpl.writeCsv(fileName)
        } returns Mono.just(mockFile)

        val expectedResponse = CryptocurrencyOuterClass.CryptocurrencyResponse.newBuilder()
            .setFile(
                CryptocurrencyOuterClass.CryptocurrencyFile.newBuilder()
                    .setFile(ByteString.copyFrom(mockFile.readBytes())).build()
            )
            .build()

        val responseMono: Mono<CryptocurrencyOuterClass.CryptocurrencyResponse> = controller.handler(request)

        StepVerifier.create(responseMono)
            .expectNext(expectedResponse)
            .verifyComplete()

        verify { cryptocurrencyServiceImpl.writeCsv(fileName) }
    }
}

