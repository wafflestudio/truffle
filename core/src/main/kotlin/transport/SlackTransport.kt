package io.wafflestudio.truffle.core.transport

import com.slack.api.Slack
import com.slack.api.methods.AsyncMethodsClient
import com.slack.api.methods.request.files.FilesUploadRequest.FilesUploadRequestBuilder
import io.wafflestudio.truffle.core.TruffleEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@ConditionalOnProperty("transport.slack.enabled", matchIfMissing = false)
@Component
class SlackTransport(
    @Value("\${transport.slack.token}") token: String,
) : TruffleTransport {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val slackClient: AsyncMethodsClient by lazy { Slack.getInstance().methodsAsync(token) }

    override suspend fun send(event: TruffleEvent) {
        val targetChannel = event.client?.slackChannel ?: return

        slackClient.filesUpload { builder -> builder.apply(event, targetChannel) }
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

    private fun FilesUploadRequestBuilder.apply(event: TruffleEvent, channel: String): FilesUploadRequestBuilder {
        if (event is TruffleEvent.V1) {
            filetype("text")
            title("${event.client?.name}-${event.client?.phase}_${LocalDateTime.now()}.txt")
            channels(listOf(channel))
            content(
                buildString {
                    val elements = event.exception.elements

                    if (elements.isNotEmpty()) {
                        elements.forEach {
                            appendLine("${it.className} in ${it.methodName} at line ${it.lineNumber}")
                        }
                    } else {
                        appendLine()
                    }
                }
            )
            initialComment("${event.exception.className} : ${event.exception.message}\n${event.description ?: ""}")
        }

        return this
    }
}
