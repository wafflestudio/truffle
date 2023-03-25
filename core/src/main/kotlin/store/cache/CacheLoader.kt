package io.wafflestudio.truffle.core.store.cache

sealed class CacheLoader<K, V> {
    abstract suspend fun load(key: K): V?
    abstract suspend fun multiLoad(keys: Collection<K>): Map<K, V>

    class SingleLoader<K, V>(private val block: suspend (K) -> V?) : CacheLoader<K, V>() {
        override suspend fun load(key: K): V? {
            return block(key)
        }

        override suspend fun multiLoad(keys: Collection<K>): Map<K, V> {
            return keys.mapNotNull { key -> block(key)?.let { key to it } }
                .toMap()
        }
    }

    class MultiLoader<K, V>(private val block: suspend (Collection<K>) -> Map<K, V>) : CacheLoader<K, V>() {
        override suspend fun load(key: K): V? {
            return block(listOf(key))[key]
        }

        override suspend fun multiLoad(keys: Collection<K>): Map<K, V> {
            return block(keys)
        }
    }
}
