package io.wafflestudio.truffle.core

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("truffle.client")
data class TruffleClientProperties(
    val info: Map<String, TruffleClientInfo>,
)

data class TruffleClientInfo(
    val apiKey: String,
    val slackChannel: String,
)
