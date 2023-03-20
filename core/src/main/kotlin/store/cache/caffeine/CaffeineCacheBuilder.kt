package io.wafflestudio.truffle.core.store.cache.caffeine

import io.wafflestudio.truffle.core.store.cache.Cache
import io.wafflestudio.truffle.core.store.cache.CacheBuilder
import io.wafflestudio.truffle.core.store.cache.CacheLoader
import java.time.Duration

class CaffeineCacheBuilder(
    private val maximumSize: Int = 1000,
) : CacheBuilder {
    override fun <K, V> build(name: String, ttl: Duration, loader: CacheLoader<K, V>): CaffeineCache<K, V> =
        CaffeineCache(name, ttl, loader, maximumSize)

    override fun <K, V> build(name: String, ttl: Duration, loader: suspend (K) -> V?): Cache<K, V> =
        build(name, ttl, CacheLoader.SingleLoader(loader))
}
