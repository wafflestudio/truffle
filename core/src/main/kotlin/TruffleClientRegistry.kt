package io.wafflestudio.truffle.core

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

@EnableConfigurationProperties(TruffleClientProperties::class)
@Service
class TruffleClientRegistry(
    clientProperties: TruffleClientProperties,
) {
    private val apiKeyToClient = clientProperties.info.entries.associate { (name, info) ->
        info.apiKey to TruffleClient(name, info.slackChannel)
    }

    fun findByApiKey(apiKey: String): TruffleClient? = apiKeyToClient[apiKey]
}
