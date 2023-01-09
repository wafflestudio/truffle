package io.wafflestudio.truffle.core

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@EnableConfigurationProperties(TruffleClientProperties::class)
@Service
class TruffleClientRegistry(
    private val clientProperties: TruffleClientProperties,
) {
    fun findByApiKey(apiKey: String): TruffleClient? = clientProperties.apiKeys[apiKey]?.let(::TruffleClient)
}
