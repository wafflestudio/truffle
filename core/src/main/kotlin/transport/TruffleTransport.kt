package io.wafflestudio.truffle.core.transport

import io.wafflestudio.truffle.core.TruffleEvent

interface TruffleTransport {
    suspend fun send(event: TruffleEvent)
}
