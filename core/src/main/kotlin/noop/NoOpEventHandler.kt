package io.wafflestudio.truffle.core.noop

import io.wafflestudio.truffle.core.TruffleEvent
import io.wafflestudio.truffle.core.TruffleEventHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class NoOpEventHandler : TruffleEventHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun handle(e: TruffleEvent) {
        logger.info("[NoOpEventHandler] consumed $e.")
    }
}
