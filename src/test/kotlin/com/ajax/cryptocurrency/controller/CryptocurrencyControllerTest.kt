package com.ajax.cryptocurrency.controller;

import com.ajax.cryptocurrency.CryptocurrencyApplication
import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.service.CryptocurrencyService
import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneOffset


@ContextConfiguration(classes = [CryptocurrencyApplication::class])
@WebMvcTest
class CryptocurrencyControllerTest {
    private val cryptocurrencies: List<String> = listOf("BTC", "ETH", "XRP")

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var cryptocurrencyController: CryptocurrencyController

    @MockBean
    private lateinit var cryptocurrencyService: CryptocurrencyService

    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val expectedCsvFile = "src/test/resources/expected.csv"
    private val id: ObjectId = ObjectId("63b346f12b207611fc867ff3")

    private val list = listOf(
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
    fun getPriceTests() {
        for (crypto in cryptocurrencies) {
            val cryptocurrencyPrice = Cryptocurrency(id, crypto, 12341f, time)
            val minPrice = cryptocurrencyPrice
            val maxPrice = cryptocurrencyPrice
            doReturn(minPrice).`when`(cryptocurrencyService)
                .findMinMaxPriceByCryptocurrencyName(crypto, 1)
            doReturn(maxPrice).`when`(cryptocurrencyService)
                .findMinMaxPriceByCryptocurrencyName(crypto, -1)

            val minPriceJson = objectMapper.writeValueAsString(minPrice)
            val maxPriceJson = objectMapper.writeValueAsString(maxPrice)

            mockMvc.perform(
                MockMvcRequestBuilders.get("/cryptocurrencies/minprice?name=$crypto")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(minPriceJson))

            mockMvc.perform(MockMvcRequestBuilders.get("/cryptocurrencies/maxprice?name=$crypto")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(maxPriceJson))
        }
    }

    @Test
    fun getPageTests() {
        for (crypto in cryptocurrencies) {
            val sortedList = list.filter { it.cryptocurrencyName == crypto }
                .sortedBy { it.price }

            doReturn(sortedList).`when`(cryptocurrencyService)
                .getCryptocurrencyPages(crypto, 0, 10)

            val sortedListJson = objectMapper.writeValueAsString(sortedList)

            mockMvc.perform(MockMvcRequestBuilders.get("/cryptocurrencies?name=$crypto")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(sortedListJson))
        }
    }

    @Test
    fun `test downloadFile method`() {
        val fileName = "test-report"
        val file = File(expectedCsvFile)
        `when`(cryptocurrencyService.writeCsv(fileName)).thenReturn(file)

        val response: ResponseEntity<FileSystemResource> = cryptocurrencyController.downloadFile(fileName)

        verify(cryptocurrencyService, times(1)).writeCsv(fileName)

        assert(response.headers.containsKey(HttpHeaders.CONTENT_DISPOSITION))
        assert(response.headers.getFirst(HttpHeaders.CONTENT_DISPOSITION) == "attachment; filename=$fileName.csv")
        assert(response.headers.contentType == MediaType.parseMediaType("text/csv"))

        val contentLength = response.body?.inputStream?.available()?.toLong()
        assertEquals(contentLength, file.length())
    }
}
