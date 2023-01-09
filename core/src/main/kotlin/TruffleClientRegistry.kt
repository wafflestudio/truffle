package io.wafflestudio.truffle.core

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TruffleClientRegistry(
    @Value("\${truffle.client}") private val clientMap: Map<String, String> = emptyMap(),
) {
    fun findByApiKey(apiKey: String): TruffleClient? = clientMap[apiKey]?.let(::TruffleClient)
}
