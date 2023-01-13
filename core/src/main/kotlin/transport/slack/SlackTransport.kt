package io.wafflestudio.truffle.core.transport.slack

import com.slack.api.Slack
import com.slack.api.methods.AsyncMethodsClient
import com.slack.api.methods.request.files.FilesUploadRequest.FilesUploadRequestBuilder
import io.wafflestudio.truffle.core.TruffleEvent
import io.wafflestudio.truffle.core.transport.TruffleTransport
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@EnableConfigurationProperties(SlackProperties::class)
@ConditionalOnProperty("transport.slack.enabled", matchIfMissing = false)
@Service
class SlackTransport(
    private val properties: SlackProperties,
) : TruffleTransport {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val slackClient: AsyncMethodsClient by lazy { Slack.getInstance().methodsAsync(properties.token) }

    override suspend fun send(event: TruffleEvent) {
        slackClient.filesUpload { builder -> builder.apply(event) }
            .thenAcceptAsync {
                if (!it.isOk) {
                    logger.error("[TruffleTransportSlackImpl] send failed. {}", it.error)
                }
            }
            .exceptionally {
                logger.error("[TruffleTransportSlackImpl] send failed", it)
                null
            }
    }

    private fun FilesUploadRequestBuilder.apply(event: TruffleEvent): FilesUploadRequestBuilder {
        val client = event.client.let(::requireNotNull)

        if (event is TruffleEvent.V1) {
            filetype("text")
            title("${client.name}_${LocalDateTime.now()}.txt")
            channels(listOf(client.slackChannel))
            content(
                buildString {
                    event.exception.elements.forEach {
                        appendLine("${it.className} in ${it.methodName} at line ${it.lineNumber}")
                    }
                }
            )
            initialComment("${event.exception.className} : ${event.exception.message}")
        }

        return this
    }
}
