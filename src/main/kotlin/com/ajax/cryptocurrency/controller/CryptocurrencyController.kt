package com.ajax.cryptocurrency.controller

import com.ajax.cryptocurrency.model.Cryptocurrency
import com.ajax.cryptocurrency.service.CryptocurrencyService
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


@RestController
@RequestMapping
@Validated
class CryptocurrencyController(private val cryptocurrencyService: CryptocurrencyService) {

    @GetMapping("/findall")
    fun getAll(): ResponseEntity<List<Cryptocurrency>> {
        return ResponseEntity.ok(cryptocurrencyService.findAll())
    }

    @GetMapping("/cryptocurrencies/minprice")
    fun getMinCryptocurrencyPrice(@RequestParam name: String): ResponseEntity<Cryptocurrency> =
        ResponseEntity.ok(cryptocurrencyService.findMinMaxPriceByCryptocurrencyName(name, 1))


    @GetMapping("/cryptocurrencies/maxprice")
    fun getMaxCryptocurrencyPrice(@RequestParam() name: String): ResponseEntity<Cryptocurrency> {
        return ResponseEntity.ok(cryptocurrencyService.findMinMaxPriceByCryptocurrencyName(name, -1))
    }

    @GetMapping("/cryptocurrencies")
    fun getCryptocurrencyByPages(
        @RequestParam(required = false) name: String?,
        @RequestParam(defaultValue = "0") @Min(0) pageNumber: Int,
        @RequestParam(defaultValue = "10") @Min(1) size: Int
    ): ResponseEntity<MutableList<Cryptocurrency?>> =
        ResponseEntity.ok(cryptocurrencyService.getCryptocurrencyPages(name, pageNumber, size))

    @GetMapping("/cryptocurrencies/csv")
    fun downloadFile(
        @RequestParam(defaultValue = "cryptocurrency-report") fileName: String
    ): ResponseEntity<FileSystemResource> {
        val file = cryptocurrencyService.writeCsv(fileName)
        val headers = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName.csv")

        return ResponseEntity.ok()
            .headers(headers)
            .contentLength(file.length())
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(FileSystemResource(file))
    }
}
