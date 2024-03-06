package com.robotutor.iot.utils.utils

import com.robotutor.iot.utils.exceptions.BaseException
import reactor.core.publisher.Mono

fun <T : Any> createMono(content: T): Mono<T> {
    return Mono.deferContextual { contextView ->
        Mono.just(content)
            .contextWrite { context -> context.putAll(contextView) }
    }
}

fun <T : Any> createMonoError(exception: BaseException): Mono<T> {
    return Mono.deferContextual { contextView ->
        Mono.error<T>(exception)
            .contextWrite { context -> context.putAll(contextView) }
    }
}
