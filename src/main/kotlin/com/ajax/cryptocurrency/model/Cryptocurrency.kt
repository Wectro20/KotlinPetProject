package com.ajax.cryptocurrency.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "statistic")
data class Cryptocurrency(
    @Id
    var id: String?,
    var cryptocurrencyName: String,
    var price: Float,
    var createdTime: LocalDateTime
)
