package com.ajax.cryptocurrency.service.convertproto

import com.google.protobuf.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun LocalDateTime.toTimestamp(): Timestamp {
    val instant = this.toInstant(ZoneOffset.UTC) 
    return Timestamp.newBuilder()
        .setSeconds(instant.epochSecond)
        .setNanos(instant.nano)
        .build()
}

fun Timestamp.toLocalDateTime(): LocalDateTime {
    val instant = Instant.ofEpochSecond(this.seconds, this.nanos.toLong())
    return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
}
