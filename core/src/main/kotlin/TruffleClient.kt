package io.wafflestudio.truffle.core

data class TruffleClient(
    val id: Long,
    val name: String,
    val phase: String,
    val slackChannel: String?,
)
