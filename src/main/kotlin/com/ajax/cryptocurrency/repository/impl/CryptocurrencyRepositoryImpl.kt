package com.ajax.cryptocurrency.repository.impl

import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.repository.CryptocurrencyRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class CryptocurrencyRepositoryImpl(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) : CryptocurrencyRepository {
    override fun save(cryptocurrency: Cryptocurrency): Mono<Cryptocurrency> {
        return reactiveMongoTemplate.save(cryptocurrency)
    }

    override fun findMinMaxByName(cryptocurrencyName: String, sort: Int): Mono<Cryptocurrency> {
        val matchCriteria = Criteria.where("cryptocurrencyName").`is`(cryptocurrencyName)
        val sortOrder = Sort.by(if (sort == 1) Sort.Order.asc("price") else Sort.Order.desc("price"))
        val query = Query().addCriteria(matchCriteria).with(sortOrder).limit(1)

        return reactiveMongoTemplate.findOne(query, Cryptocurrency::class.java)
    }

    override fun findAll(): Flux<Cryptocurrency> {
        return reactiveMongoTemplate.findAll(Cryptocurrency::class.java)
    }

    override fun findAllBy(pageable: Pageable): Flux<Cryptocurrency> {
        val query = Query()
            .with(pageable)

        return reactiveMongoTemplate.find(query, Cryptocurrency::class.java)
    }

    override fun findCryptocurrencyPriceByCryptocurrencyName(name: String, pageable: Pageable): Flux<Cryptocurrency> {
        val matchCriteria = Criteria.where("cryptocurrencyName").`is`(name)
        val query = Query().addCriteria(matchCriteria)
            .with(pageable)

        return reactiveMongoTemplate.find(query, Cryptocurrency::class.java)
    }
}
