package com.robotutor.iot.utils.filters

import com.robotutor.iot.logging.logOnSuccess
import com.robotutor.iot.logging.serializer.DefaultSerializer.serialize
import com.robotutor.iot.utils.config.AppConfig
import com.robotutor.iot.utils.exceptions.IOTError
import com.robotutor.iot.utils.exceptions.UnAuthorizedException
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.utils.utils.createMono
import com.robotutor.iot.utils.utils.createMonoError
import com.robotutor.iot.webClient.WebClientWrapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.LocalDateTime

const val AUTHORIZATION_HEADER_KEY = "AUTHORIZATION_HEADER_KEY"

@Component
class ApiFilter(
    private val routeValidator: RouteValidator,
    private val webClient: WebClientWrapper,
    private val appConfig: AppConfig
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val startTime = LocalDateTime.now()
        return authorize(exchange)
            .flatMap { userAuthenticationData ->
                if (isAllowedForAccounts(userAuthenticationData, exchange.request)) {
                    createMono(userAuthenticationData)
                } else {
                    createMonoError(UnAuthorizedException(IOTError.IOT0101))
                }
            }
            .flatMap { userAuthenticationData ->
                chain.filter(addUserAuthenticationDataIntoRequestHeader(exchange, userAuthenticationData))
            }
            .onErrorResume {
                val unAuthorizedException = UnAuthorizedException(IOTError.IOT0101)
                val response = exchange.response

                response.statusCode = HttpStatus.UNAUTHORIZED
                response.headers.contentType = MediaType.APPLICATION_JSON
                response.writeWith(
                    Mono.just(
                        response.bufferFactory().wrap(serialize(unAuthorizedException.errorResponse()).toByteArray())
                    )
                )
            }
            .publishOn(Schedulers.boundedElastic())
            .contextWrite { it.put(ServerWebExchange::class.java, exchange) }
            .contextWrite { it.put("startTime", startTime) }
            .doFinally {
                Mono.just("")
                    .logOnSuccess("Successfully send api response")
                    .contextWrite { it.put(ServerWebExchange::class.java, exchange) }
                    .contextWrite { it.put("startTime", startTime) }
                    .subscribe()
            }
    }

    private fun addUserAuthenticationDataIntoRequestHeader(
        exchange: ServerWebExchange,
        userAuthenticationData: UserAuthenticationData
    ): ServerWebExchange {
        return exchange.mutate()
            .request { requestBuilder ->
                requestBuilder.headers { httpHeaders ->
                    httpHeaders.add(AUTHORIZATION_HEADER_KEY, serialize(userAuthenticationData))
                }
            }.build()
    }

    private fun authorize(exchange: ServerWebExchange): Mono<UserAuthenticationData> {
        return if (routeValidator.isSecured(exchange.request)) {
            webClient.get(
                baseUrl = appConfig.authServiceBaseUrl,
                path = "/auth/validate",
                returnType = UserAuthenticationData::class.java,
                headers = exchange.request.headers.mapValues { it.value.joinToString(",") }
            )
        } else {
            createMono(UserAuthenticationData("Authorization not required", "account", "role"))
        }
    }

    private fun isAllowedForAccounts(
        userAuthenticationData: UserAuthenticationData,
        request: ServerHttpRequest
    ): Boolean {
        return userAuthenticationData.accountId.isNotBlank() && userAuthenticationData.roleId.isNotBlank() || routeValidator.isOpenForAccounts(
            request
        )
    }
}
