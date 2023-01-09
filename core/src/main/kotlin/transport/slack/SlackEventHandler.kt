package io.wafflestudio.truffle.core.transport.slack

import io.wafflestudio.truffle.core.TruffleEvent
import io.wafflestudio.truffle.core.TruffleEventHandler
import org.springframework.stereotype.Service

@Service
class SlackEventHandler(
    private val slackTransport: SlackTransport,
) : TruffleEventHandler {

    override suspend fun handle(e: TruffleEvent) {
        slackTransport.send(e)
    }
}
