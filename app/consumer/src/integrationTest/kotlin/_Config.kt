package io.wafflestudio.truffle

import io.wafflestudio.truffle.core.TruffleClient
import io.wafflestudio.truffle.core.TruffleClientRegistry
import io.wafflestudio.truffle.core.TruffleEventBus
import io.wafflestudio.truffle.core.TruffleEventHandler
import org.springframework.beans.factory.getBean
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@AutoConfigureWebTestClient
@SpringBootTest(classes = [ConsumerApplication::class])
annotation class IntegrationTest

@Configuration
class IntegrationTestConfig(
    private val app: ApplicationContext,
) {

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationStart() {
        val bus = app.getBean<TruffleEventBus>()
        val handlers = app.getBeansOfType(TruffleEventHandler::class.java)

        handlers.forEach { (name, handler) -> bus.subscribe(subscriberName = name, block = handler::handle) }
    }
}

@Primary
@Service
class TestClientClientRegistry : TruffleClientRegistry {
    override suspend fun findByApiKey(apiKey: String): TruffleClient? =
        if (apiKey == "test") {
            TruffleClient(id = 1, name = "integrationTest", slackChannel = "truffle-snutt-dev", phase = "development")
        } else {
            null
        }
}
