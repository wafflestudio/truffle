package io.wafflestudio.truffle.core.transport.slack

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("transport.slack")
data class SlackProperties(
    val token: String,
)
