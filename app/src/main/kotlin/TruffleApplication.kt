package io.wafflestudio.truffle

import io.wafflestudio.truffle.core.TruffleEventBus
import io.wafflestudio.truffle.core.TruffleEventHandler
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@SpringBootApplication
class TruffleApplication(
    private val eventBus: TruffleEventBus,
) {

    @Bean
    fun apiRouter() = coRouter {
        GET("/ping") { ServerResponse.ok().bodyValueAndAwait("pong") }

        // FIXME
        POST("/events") { request ->
            eventBus.publish(request.awaitBody())
            ServerResponse.ok().bodyValueAndAwait(Unit)
        }
    }
}

fun main() {
    val app = runApplication<TruffleApplication>()

    val bus = app.getBean<TruffleEventBus>()
    val handlers = app.getBeansOfType(TruffleEventHandler::class.java)

    handlers.forEach { (name, handler) -> bus.subscribe(subscriberName = name, block = handler::handle) }
}
