package com.robotutor.iot.webClient

import com.robotutor.iot.logging.logOnError
import com.robotutor.iot.logging.logOnSuccess
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class WebClientWrapper(private val webClient: WebClient) {
    fun <T> get(
        baseUrl: String,
        path: String,
        returnType: Class<T>,
        queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
        uriVariables: Map<String, Any> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        skipLoggingAdditionalDetails: Boolean = false,
        skipLoggingResponseBody: Boolean = true
    ): Mono<T> {
        val url = createUrlForRequest(baseUrl, path, uriVariables, queryParams)

        return Mono.deferContextual { ctx ->
            val exchange = ctx.get(ServerWebExchange::class.java)
            webClient.get()
                .uri(url)
                .headers { h ->
                    h.putAll(exchange.request.headers)
                    headers.map {
                        h.set(it.key, it.value)
                    }
                }
                .retrieve()
                .bodyToMono(returnType)
                .logOnSuccess(
                    message = "GET request to Service successful",
                    skipAdditionalDetails = skipLoggingAdditionalDetails,
                    skipResponseBody = skipLoggingResponseBody,
                )
                .logOnError(
                    errorCode = "API_FAILURE",
                    errorMessage = "GET request to Service failed",
                    skipAdditionalDetails = skipLoggingAdditionalDetails
                )
                .contextWrite { it.put("startTime", LocalDateTime.now()) }
        }
    }

    fun <T> post(
        baseUrl: String,
        path: String,
        body: Any,
        returnType: Class<T>,
        queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
        uriVariables: Map<String, Any> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        skipLoggingAdditionalDetails: Boolean = false,
        skipLoggingResponseBody: Boolean = true
    ): Mono<T> {

        val url = createUrlForRequest(baseUrl, path, uriVariables, queryParams)

        return Mono.deferContextual { ctx ->
            val exchange = ctx.get(ServerWebExchange::class.java)
            webClient
                .post()
                .uri(url)
                .headers { h ->
                    h.putAll(exchange.request.headers)
                    headers.map {
                        h.set(it.key, it.value)
                    }
                }.bodyValue(body)
                .retrieve()
                .bodyToMono(returnType)

                .logOnSuccess(
                    message = "POST request to Service successful",
                    skipAdditionalDetails = skipLoggingAdditionalDetails,
                    skipResponseBody = skipLoggingResponseBody,
                )
                .logOnError(
                    errorCode = "API_FAILURE",
                    errorMessage = "POST request to Service failed",
                    skipAdditionalDetails = skipLoggingAdditionalDetails,
                )
                .contextWrite { it.put("startTime", LocalDateTime.now()) }
        }
    }

    private fun createUrlForRequest(
        baseUrl: String,
        path: String,
        uriVariables: Map<String, Any>,
        queryParams: MultiValueMap<String, String>
    ): String {
        return baseUrl + UriComponentsBuilder
            .fromPath(path)
            .uriVariables(uriVariables)
            .queryParams(queryParams)
            .build()
            .toUriString()
    }
}
