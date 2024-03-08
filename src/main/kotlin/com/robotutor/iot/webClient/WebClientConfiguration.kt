package com.robotutor.iot.webClient

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableWebFlux
class WebClientConfiguration {
    @Bean
    fun webClientWrapper(): WebClientWrapper {
        return WebClientWrapper(webClient())
    }

    @Bean
    fun webClient(): WebClient = WebClient.builder()
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
        .build()
}
