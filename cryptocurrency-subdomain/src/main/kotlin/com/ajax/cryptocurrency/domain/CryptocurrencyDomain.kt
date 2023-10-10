package com.ajax.cryptocurrency.domain

import org.bson.types.ObjectId
import java.time.LocalDateTime

class CryptocurrencyDomain(
    var id: ObjectId = ObjectId(),
    var cryptocurrencyName: String,
    var price: Float,
    var createdTime: LocalDateTime
)


