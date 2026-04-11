package io.wafflestudio.truffle.core.transport

import com.slack.api.Slack
import com.slack.api.methods.AsyncMethodsClient
import com.slack.api.methods.request.files.FilesUploadV2Request
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
        val client = event.client

        if (event is TruffleEvent.V1)
            logger.info(
                "[TruffleTransportSlackImpl] sending event. clientId={}, clientName={}, clientPhase={}, channel={}, exceptionClass={}, exceptionMessage={}",
                client?.id,
                client?.name,
                client?.phase,
                targetChannel,
                event.exception.className,
                event.exception.message
            )

        slackClient.filesUploadV2 { builder -> builder.apply(event, targetChannel) }
            .thenAcceptAsync {
                if (it.isOk) {
                    logger.info(
                        "[TruffleTransportSlackImpl] send succeeded. clientId={}, clientName={}, clientPhase={}, channel={}",
                        client?.id,
                        client?.name,
                        client?.phase,
                        targetChannel,
                    )
                } else {
                    logger.error(
                        "[TruffleTransportSlackImpl] send failed. clientId={}, clientName={}, clientPhase={}, channel={}, error={}",
                        client?.id,
                        client?.name,
                        client?.phase,
                        targetChannel,
                        it.error,
                    )
                }
            }
            .exceptionally {
                logger.error(
                    "[TruffleTransportSlackImpl] send failed. clientId={}, clientName={}, clientPhase={}, channel={}",
                    client?.id,
                    client?.name,
                    client?.phase,
                    targetChannel,
                    it,
                )
                null
            }
    }

    private fun FilesUploadV2Request.FilesUploadV2RequestBuilder.apply(event: TruffleEvent, channel: String): FilesUploadV2Request.FilesUploadV2RequestBuilder {
        if (event is TruffleEvent.V1) {
            filename("${event.client?.name}-${event.client?.phase}_${LocalDateTime.now()}.txt")
            title("${event.client?.name}-${event.client?.phase}_${LocalDateTime.now()}.txt")
            channel(channel)
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
                },
            )
            initialComment("${event.exception.className} : ${event.exception.message}\n${event.description ?: ""}")
        }

        return this
    }
}
