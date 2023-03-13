package io.wafflestudio.truffle.core.transport.slack

import io.wafflestudio.truffle.core.TruffleEvent
import io.wafflestudio.truffle.core.TruffleEventHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@EnableConfigurationProperties(SlackProperties::class)
@ConditionalOnProperty("transport.slack.enabled", matchIfMissing = false)
@Service
class SlackEventHandler(
    properties: SlackProperties,
) : TruffleEventHandler {
    private val slackTransport: SlackTransport = SlackTransport(properties)

    override suspend fun handle(event: TruffleEvent) {
        slackTransport.send(event)
    }
}
