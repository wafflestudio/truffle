package io.wafflestudio.truffle.core.transport.slack

import io.wafflestudio.truffle.core.TruffleEvent
import io.wafflestudio.truffle.core.TruffleEventHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@ConditionalOnProperty("transport.slack.enabled", matchIfMissing = false)
@Service
class SlackEventHandler(
    @Value("\${transport.slack.token}") private val token: String,
) : TruffleEventHandler {
    private val slackTransport: SlackTransport = SlackTransport(token)

    override suspend fun handle(event: TruffleEvent) {
        slackTransport.send(event)
    }
}
