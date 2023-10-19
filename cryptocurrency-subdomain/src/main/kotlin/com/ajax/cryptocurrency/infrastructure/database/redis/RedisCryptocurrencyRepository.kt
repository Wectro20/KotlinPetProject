package com.ajax.cryptocurrency.infrastructure.database.redis

import com.ajax.cryptocurrency.infrastructure.mapper.CryptocurrencyMapper
import com.ajax.cryptocurrency.application.ports.repository.RedisCryptocurrencyRepositoryOutPort
import com.ajax.cryptocurrency.domain.DomainCryptocurrency
import com.ajax.cryptocurrency.infrastructure.database.mongo.entity.CryptocurrencyEntity
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class RedisCryptocurrencyRepository(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, CryptocurrencyEntity>,
    private val cryptocurrencyMapper: CryptocurrencyMapper
) : RedisCryptocurrencyRepositoryOutPort {

    override fun save(domainCryptocurrency: DomainCryptocurrency): Mono<DomainCryptocurrency> {
        return reactiveRedisTemplate.opsForValue()
            .set(domainCryptocurrency.id!!, cryptocurrencyMapper.toEntity(domainCryptocurrency))
            .thenReturn(domainCryptocurrency)
    }

    override fun findAll(): Flux<DomainCryptocurrency> {
        val scanOptions = ScanOptions.scanOptions().match("cryptocurrency:*").build()
        return reactiveRedisTemplate.scan(scanOptions)
            .flatMap { key ->
                reactiveRedisTemplate.opsForValue()
                    .get(key)
                    .map {
                        cryptocurrencyMapper.toDomain(it)
                    }
            }
    }
}
