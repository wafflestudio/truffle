package io.wafflestudio.truffle.core.transport.slack

import com.slack.api.Slack
import com.slack.api.methods.AsyncMethodsClient
import com.slack.api.methods.request.files.FilesUploadRequest.FilesUploadRequestBuilder
import io.wafflestudio.truffle.core.TruffleEvent
import io.wafflestudio.truffle.core.transport.TruffleTransport
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class SlackTransport(
    private val properties: SlackProperties,
) : TruffleTransport {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val slackClient: AsyncMethodsClient by lazy { Slack.getInstance().methodsAsync(properties.token) }

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
            title("${event.app.name}-${event.app.phase}_${LocalDateTime.now()}.txt")
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
