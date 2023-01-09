package io.wafflestudio.truffle.core

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("truffle.client")
data class TruffleClientProperties(
    val apiKeys: Map<String, String>,
)
