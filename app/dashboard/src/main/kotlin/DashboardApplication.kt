package io.wafflestudio.truffle

import io.wafflestudio.truffle.api.docs.AuthDocs
import io.wafflestudio.truffle.api.docs.ExceptionDocs
import io.wafflestudio.truffle.api.filter.ApiSecurityFilter
import io.wafflestudio.truffle.handler.AuthHandler
import io.wafflestudio.truffle.handler.ExceptionHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import java.net.URI

fun main() {
    runApplication<DashboardApplication>()
}

@SpringBootApplication
class DashboardApplication(
    private val auth: AuthHandler,
    private val exception: ExceptionHandler,
    private val securityFilter: ApiSecurityFilter,
) {
    @Bean
    fun swaggerRouter() = coRouter {
        GET("/") { temporaryRedirect(URI("/swagger-ui.html")).buildAndAwait() }
    }

    @AuthDocs
    @Bean
    fun authRouter() = coRouter {
        POST("/api/v1/auth", auth::login)
    }

    @ExceptionDocs
    @Bean
    fun exceptionRouter() = coRouter {
        GET("/api/v1/exceptions", exception::gets)
        GET("/api/v1/exceptions/{id}", exception::get)
        PATCH("/api/v1/exceptions/{id}", exception::update)
    }
        .filter(securityFilter)
}
