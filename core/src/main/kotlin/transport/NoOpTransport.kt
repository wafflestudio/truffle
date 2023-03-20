package io.wafflestudio.truffle.core.transport

import io.wafflestudio.truffle.core.TruffleEvent
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Component

@ConditionalOnMissingBean(SlackTransport::class)
@Component
class NoOpTransport : TruffleTransport {
    override suspend fun send(event: TruffleEvent) {}
}
