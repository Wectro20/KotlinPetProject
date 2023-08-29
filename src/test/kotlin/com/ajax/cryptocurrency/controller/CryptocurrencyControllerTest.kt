package com.ajax.cryptocurrency.controller;

import com.ajax.cryptocurrency.CryptocurrencyApplication
import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.service.CryptocurrencyService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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
class CryptocurrencyControllerTest(@Value("\${cryptocurrency.name}") private val cryptocurrencies: List<String>) {

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

    private val list = listOf(
        Cryptocurrency("63b346f12b207611fc867ff3", "BTC", 12341f, time),
        Cryptocurrency("63b346f12b20761zx5ft7ff3", "BTC", 23455f, time),
        Cryptocurrency("63b346f12b207611fc867ff3", "ETH", 1200f, time),
        Cryptocurrency("63b346f125106611fc867ff3", "ETH", 1300f, time),
        Cryptocurrency("63b346gt2b26544564t67ff3", "ETH", 1400f, time),
        Cryptocurrency("63b3z35de2b207611fc867ff3", "XRP", 200f, time),
        Cryptocurrency("63b346f12b207611fc789ff3", "XRP", 300f, time),
        Cryptocurrency("63b34kl42b207611fc867ff3", "XRP", 520f, time)
    )

    @Test
    fun getPriceTests() {
        for (crypto in cryptocurrencies) {
            val cryptocurrencyPrice = Cryptocurrency("63b346f12b207611fc867ff3", crypto, 12341f, time)
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
        assertEquals(response.headers.getFirst(HttpHeaders.CONTENT_DISPOSITION), "attachment; filename=$fileName.csv")
        assertEquals(response.headers.contentType, MediaType.parseMediaType("text/csv"))

        val contentLength = response.body?.inputStream?.available()?.toLong()
        assertEquals(contentLength, file.length())
    }
}
