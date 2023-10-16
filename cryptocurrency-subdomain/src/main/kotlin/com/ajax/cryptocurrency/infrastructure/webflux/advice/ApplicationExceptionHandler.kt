package com.ajax.cryptocurrency.infrastructure.webflux.advice

import com.ajax.cryptocurrency.application.exception.CryptocurrencyPriceNotFoundException
import com.ajax.cryptocurrency.application.exception.InvalidParamsForThePageException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ApplicationExceptionHandler {
    @ResponseBody
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(CryptocurrencyPriceNotFoundException::class)
    fun handleCryptocurrencyPriceNotFoundException(e: CryptocurrencyPriceNotFoundException): ErrorResponse =
        ErrorResponse(HttpStatus.NOT_FOUND, e.message ?: "Cryptocurrency price not found.")

    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidParamsForThePageException::class)
    fun handleInvalidParamsForThePageException(e: InvalidParamsForThePageException): ErrorResponse =
        ErrorResponse(HttpStatus.BAD_REQUEST, e.message ?: "Invalid params for the page")

    data class ErrorResponse (
        private val errorCode: HttpStatus,
        private val errorMessage: String
    )
}
