package io.wafflestudio.truffle

import io.wafflestudio.truffle.core.TruffleEvent
import io.wafflestudio.truffle.core.protocol.TruffleApp
import io.wafflestudio.truffle.core.protocol.TruffleException
import io.wafflestudio.truffle.core.protocol.TruffleException.Element
import io.wafflestudio.truffle.core.protocol.TruffleRuntime
import io.wafflestudio.truffle.core.protocol.TruffleVersion
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient

@IntegrationTest
class SlackTest @Autowired constructor(
    private val webTestClient: WebTestClient,
) {

    @Test
    fun send(): Unit = runBlocking {
        webTestClient.post()
            .uri("/events")
            .header("x-api-key", "test")
            .bodyValue(event)
            .exchange()
            .expectStatus()
            .isOk

        // delay for subscriber
        delay(2000)
    }

    private val event = TruffleEvent.V1(
        app = TruffleApp(name = "integrationTest", phase = "dev"),
        runtime = TruffleRuntime(name = "java", version = "17"),
        exception = TruffleException(
            className = "TruffleError",
            message = "This is for test.",
            elements = listOf(
                Element(
                    className = "io.wafflestudio.truffle.core.EventSerializeTest",
                    methodName = "eventV1",
                    lineNumber = 16,
                    fileName = "EventSerializeTest.kt",
                    isInAppInclude = true,
                ),
                Element(
                    className = "io.wafflestudio.truffle.core.EventSerializeTest",
                    methodName = "eventV2",
                    lineNumber = 19,
                    fileName = "EventSerializeTest.kt",
                    isInAppInclude = true,
                )
            )
        ),
        description = "description",
        version = TruffleVersion.V1
    )
}
