package com.ajax.cryptocurrency.domain

import org.bson.types.ObjectId
import java.time.LocalDateTime

class DomainCryptocurrency(
    var id: String?,
    var cryptocurrencyName: String,
    var price: Float,
    var createdTime: LocalDateTime
)
