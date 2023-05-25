package io.wafflestudio.truffle.api.filter

import io.wafflestudio.truffle.api.ApiError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class ApiErrorFilter : WebFilter {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return mono(Dispatchers.Unconfined) {
            chain.filter(exchange)
                .onErrorResume { ex ->
                    exchange.response.statusCode = when (ex) {
                        is ApiError -> HttpStatusCode.valueOf(ex.status)
                        is ServerWebInputException -> HttpStatus.BAD_REQUEST
                        else -> {
                            logger.error("unexpected error.", ex)
                            HttpStatus.INTERNAL_SERVER_ERROR
                        }
                    }
                    exchange.response.setComplete()
                }
                .awaitSingleOrNull()
        }
    }
}
