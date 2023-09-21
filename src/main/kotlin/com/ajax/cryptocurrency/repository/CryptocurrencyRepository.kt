package com.ajax.cryptocurrency.repository

import com.ajax.cryptocurrency.model.Cryptocurrency
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface CryptocurrencyRepository : ReactiveMongoRepository<Cryptocurrency, String> {
    @Aggregation(pipeline = [
                            "{ '\$match': { 'cryptocurrencyName' : ?0 } }",
                            "{ '\$sort' : { 'price' : ?1} }",
                            "{ '\$limit' : 1 }"
    ])
    fun findMinMaxByName(cryptocurrencyName: String, sort: Int): Mono<Cryptocurrency>

    fun findAllBy(pageable: Pageable): Flux<Cryptocurrency>
    fun findCryptocurrencyPriceByCryptocurrencyName(name: String, pageable: Pageable): Flux<Cryptocurrency>
}
