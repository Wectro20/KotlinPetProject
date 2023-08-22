package com.ajax.cryptocurrency.repository

import com.ajax.cryptocurrency.model.Cryptocurrency
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CryptocurrencyRepository : MongoRepository<Cryptocurrency, String> {
    @Aggregation(pipeline = [
                            "{ '\$match': { 'cryptocurrencyName' : ?0 } }",
                            "{ '\$sort' : { 'price' : ?1} }",
                            "{ '\$limit' : 1 }"
    ])
    fun findMinMaxByName(cryptocurrencyName: String, sort: Int): Cryptocurrency?

    fun findCryptocurrencyPriceByCryptocurrencyName(name: String, pageable: Pageable): Page<Cryptocurrency>
}
