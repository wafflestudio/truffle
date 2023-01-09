package io.wafflestudio.truffle.core.transport.slack

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("transport.slack.channels")
data class SlackProperties(
    val token: String,
)
