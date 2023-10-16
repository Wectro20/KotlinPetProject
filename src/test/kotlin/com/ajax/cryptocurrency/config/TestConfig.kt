package com.ajax.cryptocurrency.config

import com.ajax.cryptocurrency.infrastructure.convertproto.CryptocurrencyConvertor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestConfig {
    @Bean
    fun cryptocurrencyConvertor(): CryptocurrencyConvertor {
        return CryptocurrencyConvertor()
    }
}
