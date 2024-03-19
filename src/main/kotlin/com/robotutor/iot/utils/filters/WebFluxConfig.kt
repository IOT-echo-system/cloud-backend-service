package com.robotutor.iot.utils.filters

import com.robotutor.iot.utils.gateway.AccountGateway
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class WebConfig(private val accountGateway: AccountGateway) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(AuthenticationDataResolver(accountGateway))
    }
}
