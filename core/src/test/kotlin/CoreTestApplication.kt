package io.wafflestudio.truffle.core

import io.wafflestudio.truffle.core.store.cache.Cache
import io.wafflestudio.truffle.core.store.cache.CacheBuilder
import io.wafflestudio.truffle.core.store.cache.CacheLoader
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ScannedGenericBeanDefinition
import org.springframework.context.event.EventListener
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
import org.springframework.data.relational.core.mapping.Table
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.test.context.event.BeforeTestExecutionEvent
import java.time.Duration

@SpringBootApplication
class CoreTestApplication(
    private val dbClient: DatabaseClient,
) {

    @EventListener(BeforeTestExecutionEvent::class)
    fun emptyDatabase(): Unit = runBlocking {
        val tables = PathMatchingResourcePatternResolver().getResources("classpath*:**/r2dbc/*.class")
            .map { ScannedGenericBeanDefinition(CachingMetadataReaderFactory().getMetadataReader(it)).metadata }
            .filter { it.hasAnnotation(Table::class.java.name) }
            .map { it.annotations.get(Table::class.java).getValue("value").get() as String }

        tables.forEach { dbClient.sql("truncate table $it").await() }
    }
}

class NoOpCache<K, V>(
    private val cacheLoader: CacheLoader<K, V>,
) : Cache<K, V> {
    override suspend fun get(key: K): V? = cacheLoader.load(key)

    override suspend fun multiGet(keys: Collection<K>): Map<K, V> = cacheLoader.multiLoad(keys)

    override suspend fun evict(key: K) {}
}

class NoOpCacheBuilder : CacheBuilder {
    override fun <K, V> build(name: String, ttl: Duration, loader: CacheLoader<K, V>): Cache<K, V> =
        NoOpCache(loader)

    override fun <K, V> build(name: String, ttl: Duration, loader: suspend (K) -> V?): Cache<K, V> =
        NoOpCache(CacheLoader.SingleLoader(loader))
}
