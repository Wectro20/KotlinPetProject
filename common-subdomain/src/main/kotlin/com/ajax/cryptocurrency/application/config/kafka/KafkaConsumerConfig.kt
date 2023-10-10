package com.ajax.cryptocurrency.application.config.kafka

import com.ajax.cryptocurrency.CryptocurrencyOuterClass.Cryptocurrency
import com.ajax.cryptocurrency.KafkaTopic
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import java.util.Collections


@Configuration
class KafkaConsumerConfig(
    @Value("\${spring.kafka.properties.schema.registry.url}") private var schemaRegistryUrl: String,
    @Value("\${spring.kafka.bootstrap-servers}") var bootstrapServers: String
) {
    @Bean
    fun kafkaReceiver(): KafkaReceiver<String, Cryptocurrency> {
        val consumerProps = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.GROUP_ID_CONFIG to "cryptocurrency-group",
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to KafkaProtobufDeserializer::class.java,
            KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE to Cryptocurrency::class.java.name,
            "schema.registry.url" to schemaRegistryUrl
        )

        val receiverOptions = ReceiverOptions.create<String, Cryptocurrency>(consumerProps)
            .subscription(Collections.singleton(KafkaTopic.ADD_CRYPTOCURRENCY_TOPIC))

        return KafkaReceiver.create(receiverOptions)
    }
}
