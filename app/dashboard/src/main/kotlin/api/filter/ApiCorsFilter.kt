package io.wafflestudio.truffle.api.filter

import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Component
class ApiCorsFilter : CorsWebFilter(corsConfig)

private val corsConfig: CorsConfigurationSource = UrlBasedCorsConfigurationSource().apply {
    val corsConfig = CorsConfiguration()
    corsConfig.allowedOrigins = listOf("*")
    corsConfig.addAllowedMethod("GET")
    corsConfig.addAllowedMethod("POST")
    corsConfig.addAllowedMethod("PATCH")
    corsConfig.addAllowedHeader("Content-Type")
    corsConfig.addAllowedHeader("Authorization")

    registerCorsConfiguration("/**", corsConfig)
}
