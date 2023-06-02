package io.wafflestudio.truffle.core.store.cache.caffeine

import com.github.benmanes.caffeine.cache.Caffeine
import io.wafflestudio.truffle.core.store.cache.Cache
import io.wafflestudio.truffle.core.store.cache.CacheLoader
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.Optional

class CaffeineCache<K, V> internal constructor(
    private val name: String,
    private val loader: CacheLoader<K, V>,
    ttl: Duration,
    maximumSize: Int,
) : Cache<K, V> {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val caffeineCache = Caffeine.newBuilder()
        .maximumSize(maximumSize.toLong())
        .expireAfterWrite(ttl)
        .build<K, Optional<Any>>()

    override suspend fun get(key: K): V? {
        val cached = caffeineCache.getIfPresent(key)

        if (cached == null) {
            logger.debug("[CaffeineCache][$name] fetch data with key $key")

            val fetched = loader.load(key)

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
                logger.debug("[CaffeineCache][$name] fetch data with keys $keys")

                loader.multiLoad(missedKeys)
            }

        if (fetched.isNotEmpty()) {
            caffeineCache.putAll(missedKeys.associateWith { Optional.ofNullable(fetched[it]) })
        }

        return cached + fetched
    }

    override suspend fun evict(key: K) {
        caffeineCache.invalidate(key)
    }
}
