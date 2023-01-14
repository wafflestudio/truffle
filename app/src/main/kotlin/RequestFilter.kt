package io.wafflestudio.truffle

import io.wafflestudio.truffle.core.TruffleClientRegistry
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class RequestFilter(
    private val clientRegistry: TruffleClientRegistry,
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val client = exchange.request.headers.getFirst("x-api-key")?.let { clientRegistry.findByApiKey(it) }

        return if (client != null) {
            exchange.attributes["client"] = client
            chain.filter(exchange)
        } else {
            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
            exchange.response.setComplete()
        }
    }
}
