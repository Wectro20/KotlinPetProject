package com.ajax.cryptocurrency.service

import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.OffsetDateTime
import java.time.ZoneOffset

@ExtendWith(MockitoExtension::class)
class CryptocurrencyServiceTest {
    @Mock
    private lateinit var cryptocurrencyRepository: CryptocurrencyRepository

    @Suppress("UnusedPrivateProperty")
    @Spy
    private var cryptocurrencies: List<String> = listOf("BTC","ETH","XRP")

    @InjectMocks
    private lateinit var cryptocurrencyService: CryptocurrencyService

    private val expectedCsvFile = "src/test/resources/expected.csv"
    private val resultCsvFile = "./cryptocurrency-prices-test.csv"
    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val cryptocurrencyBTCPrice = Cryptocurrency("63b346f12b207611fc867ff3", "BTC", 12341f, time)
    private val cryptocurrencyETHPrice = Cryptocurrency("63b346f12b207611fc867ff3", "ETH", 12341f, time)
    private val cryptocurrencyXRPPrice = Cryptocurrency("63b346f12b207611fc867ff3", "XRP", 12341f, time)

    private val cryptocurrencyList = listOf(
        Cryptocurrency("63b346f12b207611fc867ff3", "BTC", 12341f, time),
        Cryptocurrency("63b346f12b20761zx5ft7ff3", "BTC", 23455f, time),
        Cryptocurrency("63b346f12b207611fc867ff3", "ETH", 1200f, time),
        Cryptocurrency("63b346f125106611fc867ff3", "ETH", 1300f, time),
        Cryptocurrency("63b346gt2b26544564t67ff3", "ETH", 1400f, time),
        Cryptocurrency("63b3z35de2b207611fc86ff3", "XRP", 200f, time),
        Cryptocurrency("63b346f12b207611fc789ff3", "XRP", 300f, time),
        Cryptocurrency("63b34kl42b207611fc867ff3", "XRP", 520f, time)
    )

    @Test
    fun findBTCMinPriceTest() {
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("BTC", 1))
            .thenReturn(cryptocurrencyBTCPrice)
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("BTC", 1)
        assertEquals(cryptocurrencyBTCPrice, result)
    }

    @Test
    fun findBTCMaxPriceTest() {
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("BTC", -1))
            .thenReturn(cryptocurrencyBTCPrice)
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("BTC", -1)
        assertEquals(cryptocurrencyBTCPrice, result)
    }

    @Test
    fun findETHMinPriceTest() {
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("ETH", -1))
            .thenReturn(cryptocurrencyETHPrice)
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("ETH", -1)
        assertEquals(cryptocurrencyETHPrice, result)
    }

    @Test
    fun findETHMaxPriceTest() {
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("ETH", -1))
            .thenReturn(cryptocurrencyETHPrice)
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("ETH", -1)
        assertEquals(cryptocurrencyETHPrice, result)
    }

    @Test
    fun findXRPMinPriceTest() {
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("XRP", -1))
            .thenReturn(cryptocurrencyXRPPrice)
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("XRP", -1)
        assertEquals(cryptocurrencyXRPPrice, result)
    }

    @Test
    fun findXRPMaxPriceTest() {
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("XRP", -1))
            .thenReturn(cryptocurrencyXRPPrice)
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("XRP", -1)
        assertEquals(cryptocurrencyXRPPrice, result)
    }

    @Test
    fun getBTCPageTest() {
        val sortedList = cryptocurrencyList.filter { it.cryptocurrencyName == "BTC" }
            .sortedBy { it.price }
        val page = PageRequest.of(0, 10)
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        Mockito.`when`(cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName("BTC", pageable))
            .thenReturn(toPage(sortedList, page))
        val result = cryptocurrencyService.getCryptocurrencyPages("BTC", 0, 10)
        assertEquals(sortedList, result)
    }

    @Test
    fun getETHPageTest() {
        val sortedList = cryptocurrencyList.filter { it.cryptocurrencyName == "ETH" }
            .sortedBy { it.price }
        val page = PageRequest.of(0, 10)
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        Mockito.`when`(cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName("ETH", pageable))
            .thenReturn(toPage(sortedList, page))
        val result = cryptocurrencyService.getCryptocurrencyPages("ETH", 0, 10)
        assertEquals(sortedList, result)
    }

    @Test
    fun getXRPPageTest() {
        val sortedList = cryptocurrencyList.filter { it.cryptocurrencyName == "XRP" }
            .sortedBy { it.price }
        val page = PageRequest.of(0, 10)
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        Mockito.`when`(cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName("XRP", pageable))
            .thenReturn(toPage(sortedList, page))
        val result = cryptocurrencyService.getCryptocurrencyPages("XRP", 0, 10)
        assertEquals(sortedList, result)
    }

    @Test
    fun createCSV() {
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("BTC", -1))
            .thenReturn(cryptocurrencyBTCPrice)
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("BTC", 1))
            .thenReturn(cryptocurrencyBTCPrice)
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("ETH", -1))
            .thenReturn(cryptocurrencyBTCPrice)
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("ETH", 1))
            .thenReturn(cryptocurrencyBTCPrice)
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("XRP", -1))
            .thenReturn(cryptocurrencyBTCPrice)
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("XRP", 1))
            .thenReturn(cryptocurrencyBTCPrice)

        val result = cryptocurrencyService.writeCsv("cryptocurrency-prices-test")
        val actual = isEqual(Paths.get(expectedCsvFile), Paths.get(resultCsvFile))
        result.delete()
        assertEquals(true, actual)
    }

    private fun toPage(list: List<Cryptocurrency>, pageable: Pageable): Page<Cryptocurrency> {
        val itemsInPage = list.drop(pageable.offset.toInt()).take(pageable.pageSize)
        return PageImpl(itemsInPage, pageable, list.size.toLong())
    }

    private fun isEqual(firstFile: Path, secondFile: Path): Boolean {
        if (Files.size(firstFile) != Files.size(secondFile)) {
            return false
        }
        val first = Files.readAllBytes(firstFile)
        val second = Files.readAllBytes(secondFile)
        return first.contentEquals(second)
    }
}
