package io.wafflestudio.truffle.core

import io.wafflestudio.truffle.core.store.r2dbc.AppRepository
import org.springframework.stereotype.Service

interface TruffleClientRegistry {
    suspend fun findByApiKey(apiKey: String): TruffleClient?
}

@Service
class TruffleClientRegistryImpl(
    private val appRepository: AppRepository,
) : TruffleClientRegistry {

    override suspend fun findByApiKey(apiKey: String): TruffleClient? =
        appRepository.findByApiKey(apiKey)?.let {
            TruffleClient(
                name = it.name,
                slackChannel = it.slackChannel
            )
        }
}
