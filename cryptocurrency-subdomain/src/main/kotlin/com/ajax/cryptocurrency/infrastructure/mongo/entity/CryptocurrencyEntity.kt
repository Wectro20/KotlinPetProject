package com.ajax.cryptocurrency.infrastructure.mongo.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "statistic")
data class CryptocurrencyEntity(
    @Id
    var id: ObjectId = ObjectId(),
    var cryptocurrencyName: String,
    var price: Float,
    var createdTime: LocalDateTime
)
