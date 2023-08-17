package com.ajax.cryptocurrency.exception

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
        ErrorResponse(HttpStatus.NOT_FOUND.toString(), e.message)

    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidParamsForThePageException::class)
    fun handleInvalidParamsForThePageException(e: InvalidParamsForThePageException): ErrorResponse =
        ErrorResponse(HttpStatus.BAD_REQUEST.toString(), e.message)

    data class ErrorResponse (
        private val errorCode: String? = null,
        private val errorMessage: String? = null
    )
}