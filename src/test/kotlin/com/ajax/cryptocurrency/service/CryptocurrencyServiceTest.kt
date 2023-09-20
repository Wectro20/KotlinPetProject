package com.ajax.cryptocurrency.service

import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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

@ExtendWith(MockKExtension::class)
class CryptocurrencyServiceTest {
    @MockK
    private lateinit var cryptocurrencyRepository: CryptocurrencyRepository

    private var cryptocurrencies: List<String> = listOf("BTC","ETH","XRP")

    private lateinit var cryptocurrencyService: CryptocurrencyService

    private val expectedCsvFile = "src/test/resources/expected.csv"
    private val resultCsvFile = "./cryptocurrency-prices-test.csv"
    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: ObjectId = ObjectId("63b346f12b207611fc867ff3")
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
    fun setup() {
        cryptocurrencyService = CryptocurrencyService(cryptocurrencyRepository, cryptocurrencies)
    }

    @Test
    fun findBTCMinPriceTest() {
        every { cryptocurrencyRepository.findMinMaxByName("BTC", 1) } returns cryptocurrencyBTCPrice
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("BTC", 1)
        assertEquals(cryptocurrencyBTCPrice, result)
    }

    @Test
    fun findBTCMaxPriceTest() {
        every { cryptocurrencyRepository.findMinMaxByName("BTC", -1) } returns cryptocurrencyBTCPrice
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("BTC", -1)
        assertEquals(cryptocurrencyBTCPrice, result)
    }

    @Test
    fun findETHMinPriceTest() {
        every { cryptocurrencyRepository.findMinMaxByName("ETH", -1) } returns cryptocurrencyETHPrice
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("ETH", -1)
        assertEquals(cryptocurrencyETHPrice, result)
    }

    @Test
    fun findETHMaxPriceTest() {
        every { cryptocurrencyRepository.findMinMaxByName("ETH", 1) } returns cryptocurrencyETHPrice
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("ETH", 1)
        assertEquals(cryptocurrencyETHPrice, result)
    }

    @Test
    fun findXRPMinPriceTest() {
        every { cryptocurrencyRepository.findMinMaxByName("XRP", -1) } returns cryptocurrencyXRPPrice
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("XRP", -1)
        assertEquals(cryptocurrencyXRPPrice, result)
    }

    @Test
    fun findXRPMaxPriceTest() {
        every { cryptocurrencyRepository.findMinMaxByName("XRP", 1) } returns cryptocurrencyXRPPrice
        val result = cryptocurrencyService.findMinMaxPriceByCryptocurrencyName("XRP", 1)
        assertEquals(cryptocurrencyXRPPrice, result)
    }

    @Test
    fun getBTCPageTest() {
        val sortedList = cryptocurrencyList.filter { it.cryptocurrencyName == "BTC" }
            .sortedBy { it.price }
        val page = PageRequest.of(0, 10)
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        every { cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName("BTC", pageable)
        } returns toPage(sortedList, page)
        val result = cryptocurrencyService.getCryptocurrencyPages("BTC", 0, 10)
        assertEquals(sortedList, result)
    }

    @Test
    fun getETHPageTest() {
        val sortedList = cryptocurrencyList.filter { it.cryptocurrencyName == "ETH" }
            .sortedBy { it.price }
        val page = PageRequest.of(0, 10)
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        every { cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName("ETH", pageable)
        } returns toPage(sortedList, page)
        val result = cryptocurrencyService.getCryptocurrencyPages("ETH", 0, 10)
        assertEquals(sortedList, result)
    }

    @Test
    fun getXRPPageTest() {
        val sortedList = cryptocurrencyList.filter { it.cryptocurrencyName == "XRP" }
            .sortedBy { it.price }
        val page = PageRequest.of(0, 10)
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        every { cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName("XRP", pageable)
        } returns toPage(sortedList, page)
        val result = cryptocurrencyService.getCryptocurrencyPages("XRP", 0, 10)
        assertEquals(sortedList, result)
    }

    @Test
    fun createCSV() {
        every { cryptocurrencyRepository.findMinMaxByName("BTC", -1) } returns cryptocurrencyBTCPrice
        every { cryptocurrencyRepository.findMinMaxByName("BTC", 1) } returns cryptocurrencyBTCPrice
        every { cryptocurrencyRepository.findMinMaxByName("ETH", -1) } returns cryptocurrencyETHPrice
        every { cryptocurrencyRepository.findMinMaxByName("ETH", 1) } returns cryptocurrencyETHPrice
        every { cryptocurrencyRepository.findMinMaxByName("XRP", -1) } returns cryptocurrencyXRPPrice
        every { cryptocurrencyRepository.findMinMaxByName("XRP", 1) } returns cryptocurrencyXRPPrice

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
