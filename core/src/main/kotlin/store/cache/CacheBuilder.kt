package io.wafflestudio.truffle.core.store.cache

import java.time.Duration

interface CacheBuilder {
    fun <K, V> build(name: String, ttl: Duration, loader: CacheLoader<K, V>): Cache<K, V>
    fun <K, V> build(name: String, ttl: Duration, loader: suspend (K) -> V?): Cache<K, V>
}
