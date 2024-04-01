package com.robotutor.iot.utils.filters

import com.robotutor.iot.utils.exceptions.DataNotFoundException
import com.robotutor.iot.utils.exceptions.IOTError
import com.robotutor.iot.utils.gateway.BoardGateway
import com.robotutor.iot.utils.models.BoardData
import com.robotutor.iot.utils.models.UserAuthenticationData
import com.robotutor.iot.utils.utils.createMonoError
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Configuration
class BoardDataResolver(private val boardGateway: BoardGateway) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == BoardData::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {

        return Mono.deferContextual { context ->
            val userAuthenticationData = context.get(UserAuthenticationData::class.java)
            val boardId = exchange.request.headers.getFirst("boardId") ?: "boardId"
            boardGateway.isValidBoard(userAuthenticationData, boardId)
                .map {
                    BoardData(boardId = boardId, accountId = userAuthenticationData.accountId)
                }
                .switchIfEmpty {
                    createMonoError(DataNotFoundException(IOTError.IOT0102))
                }
        }
    }
}
