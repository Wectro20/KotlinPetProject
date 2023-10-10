package com.ajax.cryptocurrency.infrastructure.mongo.repository

import com.ajax.cryptocurrency.application.mapper.CryptocurrencyMapper
import com.ajax.cryptocurrency.application.ports.repository.CryptocurrencyRepository
import com.ajax.cryptocurrency.domain.CryptocurrencyDomain
import com.ajax.cryptocurrency.infrastructure.mongo.entity.CryptocurrencyEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class CryptocurrencyRepositoryImpl(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
    private val cryptocurrencyMapper: CryptocurrencyMapper
) : CryptocurrencyRepository {
    override fun save(cryptocurrencyDomain: CryptocurrencyDomain): Mono<CryptocurrencyDomain> =
        reactiveMongoTemplate.save(cryptocurrencyMapper.toEntity(cryptocurrencyDomain))
            .map { cryptocurrencyMapper.toDomain(it) }

    override fun findAll(): Flux<CryptocurrencyDomain> =
        reactiveMongoTemplate.findAll(CryptocurrencyEntity::class.java)
            .map { cryptocurrencyMapper.toDomain(it) }

    override fun findMinMaxByName(cryptocurrencyName: String, sort: Int): Mono<CryptocurrencyDomain> {
        val matchCriteria = Criteria.where("cryptocurrencyName").`is`(cryptocurrencyName)
        val sortOrder = Sort.by(if (sort == 1) Sort.Order.asc("price") else Sort.Order.desc("price"))
        val query = Query().addCriteria(matchCriteria).with(sortOrder).limit(1)

        return reactiveMongoTemplate.findOne(query, CryptocurrencyEntity::class.java)
            .map { cryptocurrencyMapper.toDomain(it) }
    }

    override fun findAllBy(pageable: Pageable): Flux<CryptocurrencyDomain> {
        val query = Query()
            .with(pageable)

        return reactiveMongoTemplate.find(query, CryptocurrencyEntity::class.java)
            .map { cryptocurrencyMapper.toDomain(it) }
    }

    override fun findCryptocurrencyPriceByCryptocurrencyName(
        name: String,
        pageable: Pageable
    ): Flux<CryptocurrencyDomain> {
        val matchCriteria = Criteria.where("cryptocurrencyName").`is`(name)
        val query = Query().addCriteria(matchCriteria)
            .with(pageable)

        return reactiveMongoTemplate.find(query, CryptocurrencyEntity::class.java)
            .map { cryptocurrencyMapper.toDomain(it) }
    }
}
