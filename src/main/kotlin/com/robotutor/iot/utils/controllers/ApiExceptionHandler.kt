package com.robotutor.iot.utils.controllers

import com.robotutor.iot.utils.exceptions.BadDataException
import com.robotutor.iot.utils.exceptions.DataNotFoundException
import com.robotutor.iot.utils.exceptions.TooManyRequestsException
import com.robotutor.iot.utils.exceptions.UnAuthorizedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException

@ControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(BadDataException::class)
    fun handleBadDataException(ex: BadDataException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.errorResponse())
    }

    @ExceptionHandler(UnAuthorizedException::class)
    fun handleUnAuthorizedException(ex: UnAuthorizedException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.errorResponse())
    }

    @ExceptionHandler(DataNotFoundException::class)
    fun handleDataNotFoundException(ex: DataNotFoundException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.errorResponse())
    }

    @ExceptionHandler(TooManyRequestsException::class)
    fun handleException(ex: TooManyRequestsException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.errorResponse())
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidationException(ex: WebExchangeBindException): ResponseEntity<Map<String, String?>> {
        val errors = ex.bindingResult.fieldErrors.map { it.defaultMessage!! }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("errors" to errors.firstOrNull()))
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(ex: ServerWebInputException): ResponseEntity<Map<String, String?>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("errors" to ex.reason))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<String> {
        ex.printStackTrace()
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error")
    }
}
