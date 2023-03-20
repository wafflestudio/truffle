package io.wafflestudio.truffle.core.store.cache

sealed class CacheLoader<K, V> {
    class SingleLoader<K, V>(val loader: suspend (K) -> V?) : CacheLoader<K, V>()
    class MultiLoader<K, V>(val multiLoader: suspend (Collection<K>) -> Map<K, V>) : CacheLoader<K, V>()
}
