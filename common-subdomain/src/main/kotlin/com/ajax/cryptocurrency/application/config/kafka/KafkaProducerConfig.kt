package com.ajax.cryptocurrency.application.config.kafka

import com.ajax.cryptocurrency.CryptocurrencyOuterClass.Cryptocurrency
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

@Configuration
class KafkaProducerConfig(
    @Value("\${spring.kafka.bootstrap-servers}") var bootstrapServers: String,
    @Value("\${spring.kafka.properties.schema.registry.url}") private var schemaRegistryUrl: String
) {

    @Bean
    fun kafkaSender(): KafkaSender<String, Cryptocurrency> {
        val producerProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to KafkaProtobufSerializer::class.java.name,
            "schema.registry.url" to schemaRegistryUrl
        )

        val senderOptions = SenderOptions.create<String, Cryptocurrency>(producerProps)
        return KafkaSender.create(senderOptions)
    }
}
