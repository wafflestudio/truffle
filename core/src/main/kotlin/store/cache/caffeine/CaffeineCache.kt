package io.wafflestudio.truffle.core.store.cache.caffeine

import com.github.benmanes.caffeine.cache.Caffeine
import io.wafflestudio.truffle.core.store.cache.Cache
import io.wafflestudio.truffle.core.store.cache.CacheLoader
import java.time.Duration
import java.util.Optional

class CaffeineCache<K, V> internal constructor(
    val name: String,
    ttl: Duration,
    cacheLoader: CacheLoader<K, V>,
    maximumSize: Int,
) : Cache<K, V> {
    private val caffeineCache = Caffeine.newBuilder()
        .maximumSize(maximumSize.toLong())
        .expireAfterWrite(ttl)
        .build<K, Optional<Any>>()

    private val loader: suspend (K) -> V? = when (cacheLoader) {
        is CacheLoader.SingleLoader -> cacheLoader.loader
        is CacheLoader.MultiLoader -> { it -> cacheLoader.multiLoader.invoke(listOf(it))[it]!! }
    }
    private val multiLoader: suspend ((Collection<K>) -> Map<K, V>) = when (cacheLoader) {
        is CacheLoader.SingleLoader -> { keys ->
            keys.mapNotNull { key -> cacheLoader.loader.invoke(key)?.let { key to it } }
                .toMap()
        }

        is CacheLoader.MultiLoader -> cacheLoader.multiLoader
    }

    override suspend fun get(key: K): V? {
        val cached = caffeineCache.getIfPresent(key)

        if (cached == null) {
            val fetched = loader.invoke(key)

            caffeineCache.put(key, Optional.ofNullable(fetched))

            return fetched
        }

        return if (cached.isPresent) {
            cached.get() as V
        } else {
            return null
        }
    }

    override suspend fun multiGet(keys: Collection<K>): Map<K, V> {
        val cached = caffeineCache.getAllPresent(keys)
            .filter { it.value.isPresent }
            .mapValues { it.value.get() as V }

        val missedKeys = keys - cached.keys

        val fetched =
            if (missedKeys.isEmpty()) {
                emptyMap()
            } else {
                multiLoader.invoke(missedKeys)
            }

        if (fetched.isNotEmpty()) {
            caffeineCache.putAll(missedKeys.associateWith { Optional.ofNullable(fetched[it]) })
        }

        return cached + fetched
    }
}
