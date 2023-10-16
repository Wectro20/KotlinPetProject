package com.ajax.cryptocurrency.config

import com.ajax.cryptocurrency.infrastructure.grpc.CryptocurrencyGrpcService
import io.grpc.Server
import io.grpc.ServerBuilder
import io.nats.client.Connection
import io.nats.client.Nats
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono

@Configuration
class AppConfig(
    @Value("\${nats.connection.url}") private val natsUrl: String,
    @Value("\${spring.grpc.port}") private val grpcPort: Int
) {
    @Bean
    fun natsConnection(): Connection = Nats.connect(natsUrl)

    @Bean(destroyMethod = "shutdown")
    fun grpcServer(cryptocurrencyGrpcService: CryptocurrencyGrpcService): Server {
        logger.info("Creating gRPC server on port $grpcPort")

        val server = ServerBuilder.forPort(grpcPort)
            .addService(cryptocurrencyGrpcService)
            .build()

        server.start()

        logger.info("gRPC server started on port $grpcPort")

        return server
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AppConfig::class.java)
    }
}
