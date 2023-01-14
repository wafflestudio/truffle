package io.wafflestudio.truffle.core

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service

interface TruffleClientRegistry {
    fun findByApiKey(apiKey: String): TruffleClient?
}

@EnableConfigurationProperties(TruffleClientProperties::class)
@Service
class TruffleClientRegistryImpl(
    clientProperties: TruffleClientProperties,
) : TruffleClientRegistry {
    private val apiKeyToClient = clientProperties.info.entries.associate { (name, info) ->
        info.apiKey to TruffleClient(name, info.slackChannel)
    }

    override fun findByApiKey(apiKey: String): TruffleClient? = apiKeyToClient[apiKey]
}
