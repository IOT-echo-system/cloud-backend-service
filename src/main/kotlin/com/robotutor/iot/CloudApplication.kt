package com.robotutor.iot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = ["com.robotutor.iot"])
@EnableWebFlux
@EnableScheduling
class CloudApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplicationBuilder(CloudApplication::class.java).run(*args)
        }
    }
}

