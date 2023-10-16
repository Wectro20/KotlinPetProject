package com.ajax.cryptocurrency.config

import com.ajax.cryptocurrency.infrastructure.convertproto.CryptocurrencyConvertor
import com.ajax.cryptocurrency.infrastructure.mapper.CryptocurrencyMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestConfig {
    @Bean
    fun cryptocurrencyConvertor(): CryptocurrencyConvertor {
        return CryptocurrencyConvertor()
    }

    @Bean
    fun cryptocurrencyMapper(): CryptocurrencyMapper {
        return CryptocurrencyMapper()
    }
}
