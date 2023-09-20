package com.ajax.cryptocurrency.config

import io.nats.client.Connection
import io.nats.client.Nats
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NatsConfig(@Value("\${nats.connection.url}") private val natsUrl: String) {

    @Bean
    fun natsConnection(): Connection = Nats.connect(natsUrl)
}
