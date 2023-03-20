package io.wafflestudio.truffle.core

import io.wafflestudio.truffle.core.store.cache.Cache
import io.wafflestudio.truffle.core.store.cache.CacheBuilder
import io.wafflestudio.truffle.core.store.cache.caffeine.CaffeineCacheBuilder
import io.wafflestudio.truffle.core.store.r2dbc.AppRepository
import org.springframework.stereotype.Service
import java.time.Duration

interface TruffleClientRegistry {
    suspend fun findByApiKey(apiKey: String): TruffleClient?
}

@Service
class TruffleClientRegistryImpl(
    private val appRepository: AppRepository,
    cacheBuilder: CacheBuilder = CaffeineCacheBuilder(),
) : TruffleClientRegistry {

    override suspend fun findByApiKey(apiKey: String): TruffleClient? =
        appCache.get(apiKey)

    private val appCache: Cache<String, TruffleClient> = cacheBuilder.build(
        name = "TruffleClientRegistry:AppCache",
        ttl = Duration.ofHours(1),
    ) { apiKey ->
        appRepository.findByApiKey(apiKey)?.let { TruffleClient(it.id, it.name, it.phase, it.slackChannel) }
    }
}
