package com.ajax.cryptocurrency.infrastructure.webflux

import com.ajax.cryptocurrency.domain.CryptocurrencyDomain
import com.ajax.cryptocurrency.infrastructure.mongo.entity.CryptocurrencyEntity
import com.ajax.cryptocurrency.infrastructure.service.CryptocurrencyServiceImpl
import jakarta.validation.constraints.Min
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/cryptocurrencies")
@Validated
class CryptocurrencyController(private val cryptocurrencyServiceImpl: CryptocurrencyServiceImpl) {

    @GetMapping("/findall")
    fun getAll(): Flux<CryptocurrencyDomain> = cryptocurrencyServiceImpl.findAll()

    @GetMapping("/minprice")
    fun getMinCryptocurrencyPrice(@RequestParam name: String): Mono<CryptocurrencyDomain> =
        cryptocurrencyServiceImpl.findMinMaxPriceByCryptocurrencyName(name, 1)

    @GetMapping("/maxprice")
    fun getMaxCryptocurrencyPrice(@RequestParam() name: String): Mono<CryptocurrencyDomain> =
        cryptocurrencyServiceImpl.findMinMaxPriceByCryptocurrencyName(name, -1)

    @GetMapping
    fun getCryptocurrencyByPages(
        @RequestParam(required = false) name: String?,
        @RequestParam(defaultValue = "0") @Min(0) pageNumber: Int,
        @RequestParam(defaultValue = "10") @Min(1) size: Int
    ): Flux<CryptocurrencyDomain> = cryptocurrencyServiceImpl.getCryptocurrencyPages(name, pageNumber, size)


    @GetMapping("/csv")
    fun downloadFile(
        @RequestParam(defaultValue = "cryptocurrency-report") fileName: String
    ): Mono<ResponseEntity<FileSystemResource>> {
        return cryptocurrencyServiceImpl.writeCsv(fileName).map { file ->
                val headers = HttpHeaders()
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName.csv")

                ResponseEntity.ok().headers(headers).contentLength(file.length())
                    .contentType(MediaType.parseMediaType("text/csv")).body(FileSystemResource(file))
            }
    }
}
