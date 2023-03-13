package io.wafflestudio.truffle.core

fun interface TruffleEventHandler {
    suspend fun handle(event: TruffleEvent)
}
