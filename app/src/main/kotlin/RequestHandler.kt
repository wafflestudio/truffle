package io.wafflestudio.truffle

import io.wafflestudio.truffle.core.TruffleClient
import io.wafflestudio.truffle.core.TruffleEvent
import io.wafflestudio.truffle.core.TruffleEventBus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Service
class RequestHandler(
    private val eventBus: TruffleEventBus,
) {

    suspend fun handle(request: ServerRequest): ServerResponse {
        val event = request.awaitBody<TruffleEvent>()

        when (event) {
            is TruffleEvent.V1 -> {
                if (event.exception.elements.isEmpty()) {
                    return ServerResponse.badRequest().bodyValueAndAwait(Unit)
                }
            }

            else -> {
                TODO("This code should be unreachable")
            }
        }

        event.client = request.attribute("client").get() as TruffleClient // This should not fail.

        eventBus.publish(event)

        return ServerResponse.ok().bodyValueAndAwait(Unit)
    }
}
