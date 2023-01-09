package io.wafflestudio.truffle.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.wafflestudio.truffle.core.protocol.TruffleApp
import io.wafflestudio.truffle.core.protocol.TruffleException
import io.wafflestudio.truffle.core.protocol.TruffleException.Element
import io.wafflestudio.truffle.core.protocol.TruffleRuntime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

class EventSerializeTest {

    @Test
    fun eventV1() {
        val mapper = Jackson2ObjectMapperBuilder().build<ObjectMapper>()

        val eventV1: TruffleEvent = TruffleEvent.V1(
            app = TruffleApp(name = "siksha", phase = "prod"),
            runtime = TruffleRuntime(
                name = "java",
                version = "17"
            ),
            exception = TruffleException(
                elements = listOf(
                    Element(
                        className = "io.wafflestudio.truffle.core.EventSerializeTest",
                        methodName = "eventV1",
                        lineNumber = 16,
                        fileName = "EventSerializeTest.kt",
                        isInAppInclude = true,
                    )
                )
            ),
            version = "v1"
        )

        val str = mapper.writeValueAsString(eventV1)

        assertThat(mapper.readValue<TruffleEvent.V1>(str)).isEqualTo(eventV1)
    }
}
