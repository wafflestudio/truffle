package io.wafflestudio.truffle.core.store

import com.fasterxml.jackson.databind.ObjectMapper
import io.wafflestudio.truffle.core.NoOpCacheBuilder
import io.wafflestudio.truffle.core.TruffleClient
import io.wafflestudio.truffle.core.TruffleEvent
import io.wafflestudio.truffle.core.protocol.TruffleException
import io.wafflestudio.truffle.core.protocol.TruffleRuntime
import io.wafflestudio.truffle.core.store.r2dbc.ExceptionEventRepository
import io.wafflestudio.truffle.core.store.r2dbc.ExceptionRepository
import io.wafflestudio.truffle.core.transport.TruffleTransport
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class StoreEventHandlerTest @Autowired constructor(
    private val exceptionRepository: ExceptionRepository,
    private val exceptionEventRepository: ExceptionEventRepository,
    private val mapper: ObjectMapper,
    transport: TruffleTransport,
) {
    private val handler = StoreEventHandler(
        exceptionRepository = exceptionRepository,
        exceptionEventRepository = exceptionEventRepository,
        transport = transport,
        mapper = mapper,
        cacheBuilder = NoOpCacheBuilder()
    )
    val protoEvent = TruffleEvent.V1(
        runtime = TruffleRuntime(
            name = "java",
            version = "17"
        ),
        exception = TruffleException(
            className = "TruffleException",
            message = "This is for Test",
            elements = listOf(
                TruffleException.Element(
                    className = "io.wafflestudio.truffle.core.protocol.EventSerializeTest",
                    methodName = "eventV1",
                    lineNumber = 16,
                    fileName = "protocol/EventSerializeTest.kt",
                    isInAppInclude = true,
                )
            )
        ),
    )

    @Test
    fun `익셉션을 발생시킨 클라이언트 식별되지 않으면 예외 처리한다`(): Unit = runBlocking {
        // Given
        val noClientEvent = protoEvent
        noClientEvent.client = null

        // When, Then
        assertThrows<IllegalStateException> {
            handler.handle(noClientEvent)
        }
    }

    @Test
    fun `새로운 익셉션의 경우, 익셉션과 이벤트를 동시에 저장한다`(): Unit = runBlocking {
        // Given
        val clientEvent = protoEvent
        val client = TruffleClient(id = 1, name = "testApp", phase = "production", slackChannel = null)
        clientEvent.client = client

        // When
        handler.handle(clientEvent)

        // Then
        val exceptions = exceptionRepository.findAll().toList()

        assertThat(exceptions.size).isEqualTo(1)

        val savedException = exceptions.first()

        savedException.run {
            assertThat(appId).isEqualTo(client.id)
            assertThat(className).isEqualTo(clientEvent.exception.className)
            assertThat(elements).isEqualTo(mapper.writeValueAsString(clientEvent.exception.elements))
            assertThat(hashCode).isEqualTo(mapper.writeValueAsString(clientEvent.exception.elements).hashCode())
        }

        val exceptionEvents = exceptionEventRepository.findAll().toList()

        assertThat(exceptionEvents.size).isEqualTo(1)

        val savedExceptionEvents = exceptionEvents.first()

        savedExceptionEvents.run {
            assertThat(exceptionId).isEqualTo(savedException.id)
            assertThat(message).isEqualTo(clientEvent.exception.message)
        }
    }

    @Test
    fun `이전에 발생한 익셉션의 경우, 이벤트만 저장한다`(): Unit = runBlocking {
        // Given
        val clientEvent = protoEvent
        val client = TruffleClient(id = 1, name = "testApp", phase = "production", slackChannel = null)
        clientEvent.client = client

        handler.handle(clientEvent)

        // When
        handler.handle(clientEvent)

        // Then
        val exceptions = exceptionRepository.findAll().toList()

        assertThat(exceptions.size).isEqualTo(1)

        val savedException = exceptions.first()

        savedException.run {
            assertThat(appId).isEqualTo(client.id)
            assertThat(className).isEqualTo(clientEvent.exception.className)
            assertThat(elements).isEqualTo(mapper.writeValueAsString(clientEvent.exception.elements))
            assertThat(hashCode).isEqualTo(mapper.writeValueAsString(clientEvent.exception.elements).hashCode())
        }

        val exceptionEvents = exceptionEventRepository.findAll().toList()

        assertThat(exceptionEvents.size).isEqualTo(2)
        exceptionEvents.forEach {
            assertThat(it.exceptionId).isEqualTo(savedException.id)
            assertThat(it.message).isEqualTo(clientEvent.exception.message)
        }
    }
}
