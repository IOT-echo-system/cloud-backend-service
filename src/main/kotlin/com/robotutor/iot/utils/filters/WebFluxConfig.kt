package com.robotutor.iot.utils.filters

import com.robotutor.iot.utils.gateway.PolicyGateway
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class WebConfig(private val policyGateway: PolicyGateway) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(AuthenticationDataResolver(policyGateway))
    }
}
