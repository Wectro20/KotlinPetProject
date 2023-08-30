package com.ajax.cryptocurrency.service

import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkConstructor
import org.apache.coyote.http11.Constants.a
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.springframework.data.domain.*
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
        every { cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName("BTC", pageable)} returns toPage(sortedList, page)
        val result = cryptocurrencyService.getCryptocurrencyPages("BTC", 0, 10)
        assertEquals(sortedList, result)
    }

    @Test
    fun getETHPageTest() {
        val sortedList = cryptocurrencyList.filter { it.cryptocurrencyName == "ETH" }
            .sortedBy { it.price }
        val page = PageRequest.of(0, 10)
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        every { cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName("ETH", pageable)} returns toPage(sortedList, page)
        val result = cryptocurrencyService.getCryptocurrencyPages("ETH", 0, 10)
        assertEquals(sortedList, result)
    }

    @Test
    fun getXRPPageTest() {
        val sortedList = cryptocurrencyList.filter { it.cryptocurrencyName == "XRP" }
            .sortedBy { it.price }
        val page = PageRequest.of(0, 10)
        val pageable = PageRequest.of(0, 10, Sort.by("price"))
        every { cryptocurrencyRepository.findCryptocurrencyPriceByCryptocurrencyName("XRP", pageable)} returns toPage(sortedList, page)
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
