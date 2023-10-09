package com.ajax.cryptocurrency.infrastructure.grpc

import com.ajax.cryptocurrency.CryptocurrencyOuterClass
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.Cryptocurrency
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyRequest
import com.ajax.cryptocurrency.CryptocurrencyOuterClass.CryptocurrencyResponse
import com.ajax.cryptocurrency.ReactorCryptocurrencyServiceGrpc.CryptocurrencyServiceImplBase
import com.ajax.cryptocurrency.application.convertproto.CryptocurrencyConvertor
import com.ajax.cryptocurrency.application.ports.service.CryptocurrencyService
import com.ajax.cryptocurrency.infrastructure.shared.stream.SharedStream
import com.google.protobuf.ByteString
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class CryptocurrencyGrpcService(
    private val cryptocurrencyService: CryptocurrencyService,
    private val cryptocurrencyConvertor: CryptocurrencyConvertor,
    private val sharedStream: SharedStream
) : CryptocurrencyServiceImplBase() {

    override fun getCryptocurrencyByName(
        request: Mono<CryptocurrencyRequest>
    ): Flux<Cryptocurrency> {
        return request.flatMapMany { cryptoRequest ->
            val name = cryptoRequest.name.name
            sharedStream.cryptocurrencies.filter { crypto ->
                crypto.cryptocurrencyName == name
            }
        }
    }


    override fun findAllCryptocurrencies(
        request: Mono<CryptocurrencyRequest>
    ): Mono<CryptocurrencyResponse> {
        return request.flatMap {
            cryptocurrencyService.findAll()
                .map { cryptocurrencyConvertor.cryptocurrencyToProto(it) }
                .collectList()
                .map { allCryptocurrency ->
                    CryptocurrencyResponse.newBuilder().apply {
                        cryptocurrencyList = CryptocurrencyOuterClass.CryptocurrencyList.newBuilder()
                            .addAllCryptocurrency(allCryptocurrency)
                            .build()
                    }.build()
                }
        }
    }


    override fun findMinMaxPriceByCryptocurrencyName(
        request: Mono<CryptocurrencyRequest>
    ): Mono<CryptocurrencyResponse> {
        return request.flatMap {
            cryptocurrencyService.findMinMaxPriceByCryptocurrencyName(
                it.cryptocurrencyMinMax.name,
                it.cryptocurrencyMinMax.sortOrder
            ).map { crypto ->
                CryptocurrencyResponse.newBuilder()
                    .setCryptocurrency(cryptocurrencyConvertor.cryptocurrencyToProto(crypto))
                    .build()
            }
        }
    }

    override fun getCryptocurrencyPages(
        request: Mono<CryptocurrencyRequest>
    ): Mono<CryptocurrencyResponse> {
        return request.flatMap {
            cryptocurrencyService.findAll()
                .map { cryptocurrencyConvertor.cryptocurrencyToProto(it) }
                .collectList()
                .map { allCryptocurrency ->
                    CryptocurrencyResponse.newBuilder().apply {
                        cryptocurrencyList = CryptocurrencyOuterClass.CryptocurrencyList.newBuilder()
                            .addAllCryptocurrency(allCryptocurrency)
                            .build()
                    }.build()
                }
        }
    }

    override fun writeCsvFile(
        request: Mono<CryptocurrencyRequest>
    ): Mono<CryptocurrencyResponse> {
        return request.flatMap {
            cryptocurrencyService.writeCsv(it.name.name).map { file ->
                val fileBytes = ByteString.copyFrom(file.readBytes())
                CryptocurrencyResponse.newBuilder().apply {
                    fileBuilder.setFile(fileBytes)
                }.build()
            }
        }
    }
}
