package com.ajax.cryptocurrency.service

import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
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


@SpringBootTest(classes = [CryptocurrencyService::class])
class CryptocurrencyServiceTest {
    @MockBean
    private lateinit var cryptocurrencyRepository: CryptocurrencyRepository

    @Autowired
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
        Assertions.assertEquals(cryptocurrencyBTCPrice, result)
    }

    @Test
    fun findBTCMaxPriceTest() {
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("BTC", -1))
            .thenReturn(cryptocurrencyBTCPrice)
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("BTC", -1)
        Assertions.assertEquals(cryptocurrencyBTCPrice, result)
    }

    @Test
    fun findETHMinPriceTest() {
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("ETH", -1))
            .thenReturn(cryptocurrencyETHPrice)
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("ETH", -1)
        Assertions.assertEquals(cryptocurrencyETHPrice, result)
    }

    @Test
    fun findETHMaxPriceTest() {
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("ETH", -1))
            .thenReturn(cryptocurrencyETHPrice)
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("ETH", -1)
        Assertions.assertEquals(cryptocurrencyETHPrice, result)
    }

    @Test
    fun findXRPMinPriceTest() {
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("XRP", -1))
            .thenReturn(cryptocurrencyXRPPrice)
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("XRP", -1)
        Assertions.assertEquals(cryptocurrencyXRPPrice, result)
    }

    @Test
    fun findXRPMaxPriceTest() {
        Mockito.`when`(cryptocurrencyRepository.findMinMaxByName("XRP", -1))
            .thenReturn(cryptocurrencyXRPPrice)
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("XRP", -1)
        Assertions.assertEquals(cryptocurrencyXRPPrice, result)
    }

    @Test
    fun getBTCPageTest() {
        val sortedList = cryptocurrencyList.filter { it.cryptocurrencyName == "BTC" }
            .sortedBy { it.price }
        val page = PageRequest.of(0, 10)
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        Mockito.`when`(cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName("BTC", pageable))
            .thenReturn(toPage(sortedList, page))
        Mockito.`when`(cryptocurrencyRepository.findAll())
            .thenReturn(cryptocurrencyList)
        val result = cryptocurrencyService.getCryptocurrencyPages("BTC", 0, 10)
        Assertions.assertEquals(sortedList, result)
    }

    @Test
    fun getETHPageTest() {
        val sortedList = cryptocurrencyList.filter { it.cryptocurrencyName == "ETH" }
            .sortedBy { it.price }
        val page = PageRequest.of(0, 10)
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        Mockito.`when`(cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName("ETH", pageable))
            .thenReturn(toPage(sortedList, page))
        Mockito.`when`(cryptocurrencyRepository.findAll())
            .thenReturn(cryptocurrencyList)
        val result = cryptocurrencyService.getCryptocurrencyPages("ETH", 0, 10)
        Assertions.assertEquals(sortedList, result)
    }

    @Test
    fun getXRPPageTest() {
        val sortedList = cryptocurrencyList.filter { it.cryptocurrencyName == "XRP" }
            .sortedBy { it.price }
        val page = PageRequest.of(0, 10)
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        Mockito.`when`(cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName("XRP", pageable))
            .thenReturn(toPage(sortedList, page))
        Mockito.`when`(cryptocurrencyRepository.findAll())
            .thenReturn(sortedList)
        val result = cryptocurrencyService.getCryptocurrencyPages("XRP", 0, 10)
        Assertions.assertEquals(sortedList, result)
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
        Assertions.assertEquals(true, actual)
    }

    private fun toPage(list: List<Cryptocurrency>, pageable: Pageable): Page<Cryptocurrency> {
        val start = pageable.offset.toInt()
        val end = minOf(start + pageable.pageSize, list.size)
        return if (start > list.size) {
            PageImpl(emptyList<Cryptocurrency>(), pageable, list.size.toLong())
        } else {
            PageImpl(list.subList(start, end), pageable, list.size.toLong())
        }
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
