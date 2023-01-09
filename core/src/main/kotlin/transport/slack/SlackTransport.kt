package io.wafflestudio.truffle.core.transport.slack

import com.slack.api.Slack
import com.slack.api.methods.AsyncMethodsClient
import com.slack.api.methods.response.files.FilesUploadResponse
import io.wafflestudio.truffle.core.TruffleEvent
import io.wafflestudio.truffle.core.transport.TruffleTransport
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import kotlin.coroutines.suspendCoroutine

@EnableConfigurationProperties(SlackProperties::class)
@ConditionalOnProperty("transport.slack.enabled", matchIfMissing = false)
@Service
class SlackTransport(
    private val properties: SlackProperties,
) : TruffleTransport {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val slackClient: AsyncMethodsClient by lazy { Slack.getInstance().methodsAsync(properties.token) }

    override suspend fun send(event: TruffleEvent) {
        runCatching {
            suspendCoroutine { cont ->
                val future = slackClient.filesUpload { builder ->
                    builder.channels(listOf(event.client!!.channelName)) // FIXME !!
                        .filetype("text")
                        .filename("TODO.txt") // event.app + time ->
                        .content("TODO") // event ->
                        .initialComment("TODO") // event ->
                }

                cont.resumeWith(runCatching { future.get() as FilesUploadResponse })
            }
        }
            .onSuccess {
                if (!it.isOk) {
                    logger.error("[TruffleTransportSlackImpl] send failed. {}", it.error)
                }
            }
            .onFailure {
                logger.error("[TruffleTransportSlackImpl] send failed.", it)
            }
    }
}
