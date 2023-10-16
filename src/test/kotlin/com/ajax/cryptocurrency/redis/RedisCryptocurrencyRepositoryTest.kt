package com.ajax.cryptocurrency.redis

import com.ajax.cryptocurrency.config.TestConfig
import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import com.ajax.cryptocurrency.infrastructure.database.mongo.entity.CryptocurrencyEntity
import com.ajax.cryptocurrency.infrastructure.database.redis.RedisCryptocurrencyRepository
import com.ajax.cryptocurrency.infrastructure.mapper.CryptocurrencyMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.OffsetDateTime
import java.time.ZoneOffset

@SpringBootTest
@ContextConfiguration(classes = [TestConfig::class])
class RedisCryptocurrencyRepositoryTest {

    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, CryptocurrencyEntity> = mockk()

    @Autowired
    private lateinit var cryptocurrencyMapper: CryptocurrencyMapper

    private lateinit var redisCryptocurrencyRepository: RedisCryptocurrencyRepository


    private val time = OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime()
    private val id: String = "63b346f12b207611fc867ff3"

    @BeforeEach
    fun setup() {
        redisCryptocurrencyRepository = RedisCryptocurrencyRepository(
            reactiveRedisTemplate,
            cryptocurrencyMapper
        )
    }

    @Test
    fun `should save domainCryptocurrency to Redis and return the same instance`() {
        val domainCryptocurrency = DomainCryptocurrency(id, "BTC", 12341f, time)

        every {
            reactiveRedisTemplate.opsForValue().set(eq(id), any())
        } returns Mono.just(true)

        val result = redisCryptocurrencyRepository.save(domainCryptocurrency)

        StepVerifier.create(result)
            .expectNext(domainCryptocurrency)
            .verifyComplete()

        verify {
            reactiveRedisTemplate.opsForValue().set(eq(id), any())
        }
    }

    @Test
    fun `should find all domainCryptocurrencies from Redis and map to DomainCryptocurrency`() {
        val key1 = "cryptocurrency:1"
        val key2 = "cryptocurrency:2"
        val cryptocurrencyEntity1 = CryptocurrencyEntity(ObjectId(id), "BTC", 12341f, time)
        val cryptocurrencyEntity2 = CryptocurrencyEntity(ObjectId(id), "ETH", 12341f, time)
        val domainCryptocurrency1 = DomainCryptocurrency(id, "BTC", 12341f, time)
        val domainCryptocurrency2 = DomainCryptocurrency(id, "ETH", 12341f, time)

        every { reactiveRedisTemplate.scan(any()) } returns Flux.just(key1, key2)
        every { reactiveRedisTemplate.opsForValue().get(key1) } returns Mono.just(cryptocurrencyEntity1)
        every { reactiveRedisTemplate.opsForValue().get(key2) } returns Mono.just(cryptocurrencyEntity2)

        val result = redisCryptocurrencyRepository.findAll()

        StepVerifier.create(result)
            .assertNext {
                assertEquals(it.id, domainCryptocurrency1.id)
                assertEquals(it.cryptocurrencyName, domainCryptocurrency1.cryptocurrencyName)
                assertEquals(it.price, domainCryptocurrency1.price)
                assertEquals(it.createdTime, domainCryptocurrency1.createdTime)
            }
            .assertNext {
                assertEquals(it.id, domainCryptocurrency2.id)
                assertEquals(it.cryptocurrencyName, domainCryptocurrency2.cryptocurrencyName)
                assertEquals(it.price, domainCryptocurrency2.price)
                assertEquals(it.createdTime, domainCryptocurrency2.createdTime)
            }
            .verifyComplete()

        verify { reactiveRedisTemplate.scan(any()) }
        verify { reactiveRedisTemplate.opsForValue().get(key1) }
        verify { reactiveRedisTemplate.opsForValue().get(key2) }
    }
}
