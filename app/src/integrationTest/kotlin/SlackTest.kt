package io.wafflestudio.truffle

import io.wafflestudio.truffle.core.TruffleEvent
import io.wafflestudio.truffle.core.protocol.TruffleApp
import io.wafflestudio.truffle.core.protocol.TruffleException
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
            .bodyValue(
                TruffleEvent.V1(
                    app = TruffleApp(name = "integrationTest", phase = "dev"),
                    runtime = TruffleRuntime(name = "java", version = "17"),
                    exception = TruffleException(elements = listOf()),
                    version = TruffleVersion.V1
                )
            )
            .exchange()
            .expectStatus()
            .isOk

        // delay for subscriber
        delay(2000)
    }
}
