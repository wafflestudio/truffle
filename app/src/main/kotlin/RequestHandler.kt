package io.wafflestudio.truffle

import io.wafflestudio.truffle.core.TruffleClientRegistry
import io.wafflestudio.truffle.core.TruffleEvent
import io.wafflestudio.truffle.core.TruffleEventBus
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Service
class RequestHandler(
    private val eventBus: TruffleEventBus,
    private val clientRegistry: TruffleClientRegistry,
) {

    suspend fun handle(request: ServerRequest): ServerResponse {
        // FIXME: WebFilter
        val apiKey = runCatching {
            requireNotNull(request.headers().firstHeader("x-api-key"))
        }.getOrElse {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValueAndAwait(Unit)
        }

        val client = clientRegistry.findByApiKey(apiKey)
        val event = request.awaitBody<TruffleEvent>().also { it.client = client }

        eventBus.publish(event)

        return ServerResponse.ok().bodyValueAndAwait(Unit)
    }
}
