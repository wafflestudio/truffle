package io.wafflestudio.truffle.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TruffleEventBus {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val eventBusScope = CoroutineScope(Dispatchers.Default)
    private val events = MutableSharedFlow<TruffleEvent>(
        replay = 0,
        extraBufferCapacity = 1000,
        onBufferOverflow = BufferOverflow.SUSPEND // FIXME
    )

    fun publish(event: TruffleEvent) {
        if (!events.tryEmit(event)) {
            logger.warn("[TruffleEventBus] bufferOverflow. Discarded {}.", event)
        }
    }

    fun subscribe(subscriberName: String, block: suspend (event: TruffleEvent) -> Unit) {
        eventBusScope.launch(SupervisorJob()) {
            events.collect {
                try {
                    block(it)
                } catch (e: Exception) {
                    logger.error("[TruffleEventBus] $subscriberName collect failed. {}", e)
                }
            }
        }
    }
}
