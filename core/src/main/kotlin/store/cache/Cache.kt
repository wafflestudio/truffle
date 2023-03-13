package io.wafflestudio.truffle.core.store.cache

interface Cache<K, V> {
    suspend fun get(key: K): V?
    suspend fun multiGet(keys: Collection<K>): Map<K, V>
}
