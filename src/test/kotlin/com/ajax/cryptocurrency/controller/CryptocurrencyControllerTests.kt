package com.ajax.cryptocurrency.controller;

import com.ajax.cryptocurrency.CryptocurrencyApplication
import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import com.ajax.cryptocurrency.service.CryptocurrencyService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneOffset

@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
@ContextConfiguration(classes = [CryptocurrencyApplication::class])
@WebMvcTest
class CryptocurrencyControllerTest {

    @Autowired

    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var cryptocurrencyService: CryptocurrencyService

    @MockBean
    private lateinit var cryptocurrencysRepository: CryptocurrencyRepository

    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val CSV_FILE = "./cryptocurrency-report.csv"

    private val cryptocurrencyPrice = Cryptocurrency("63b346f12b207611fc867ff3", "BTC", 12341f, time)

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
        val cryptocurrencies = listOf("BTC", "ETH", "XRP")

        for (crypto in cryptocurrencies) {
            val minPrice = cryptocurrencyPrice
            val maxPrice = cryptocurrencyPrice
            Mockito.doReturn(minPrice).`when`(cryptocurrencyService)
                .findMinMaxPriceByCryptocurrencyName(crypto, 1)
            Mockito.doReturn(maxPrice).`when`(cryptocurrencyService)
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
        val cryptocurrencies = listOf("BTC", "ETH", "XRP")

        for (crypto in cryptocurrencies) {
            val sortedList = list.filter { it.cryptocurrencyName == crypto }
                .sortedBy { it.price }

            Mockito.doReturn(sortedList).`when`(cryptocurrencyService)
                .getCryptocurrencyPages(crypto, 0, 10)

            val sortedListJson = objectMapper.writeValueAsString(sortedList)

            mockMvc.perform(MockMvcRequestBuilders.get("/cryptocurrencies?name=$crypto")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(sortedListJson))
        }
    }
}