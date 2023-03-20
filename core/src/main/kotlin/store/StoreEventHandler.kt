package io.wafflestudio.truffle.core.store

import com.fasterxml.jackson.databind.ObjectMapper
import io.wafflestudio.truffle.core.TruffleEvent
import io.wafflestudio.truffle.core.TruffleEventHandler
import io.wafflestudio.truffle.core.store.cache.Cache
import io.wafflestudio.truffle.core.store.cache.CacheBuilder
import io.wafflestudio.truffle.core.store.cache.CacheLoader
import io.wafflestudio.truffle.core.store.cache.caffeine.CaffeineCacheBuilder
import io.wafflestudio.truffle.core.store.r2dbc.ExceptionEventRepository
import io.wafflestudio.truffle.core.store.r2dbc.ExceptionEventTable
import io.wafflestudio.truffle.core.store.r2dbc.ExceptionRepository
import io.wafflestudio.truffle.core.store.r2dbc.ExceptionTable
import io.wafflestudio.truffle.core.transport.TruffleTransport
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class StoreEventHandler(
    private val exceptionRepository: ExceptionRepository,
    private val exceptionEventRepository: ExceptionEventRepository,
    private val transport: TruffleTransport,
    private val mapper: ObjectMapper,
    private val mutex: Mutex = Mutex(),
    cacheBuilder: CacheBuilder = CaffeineCacheBuilder(maximumSize = 100),
) : TruffleEventHandler {
    private val exceptionTableCache: Cache<ExceptionTableCacheKey, ExceptionTable> = cacheBuilder.build(
        name = "StoreEventHandler:ExceptionCache",
        ttl = Duration.ofHours(1),
        loader = CacheLoader.SingleLoader { mutex.withLock { exceptionRepository.getOrCreate(it) } }
    )

    override suspend fun handle(event: TruffleEvent) = when (event) {
        is TruffleEvent.V1 -> handleV1(event)
        else -> error("not reachable")
    }

    private suspend fun handleV1(event: TruffleEvent.V1) {
        val client = checkNotNull(event.client)
        val exception = event.exception

        val exceptionTableCacheKey = ExceptionTableCacheKey(
            appId = client.id,
            className = exception.className,
            elements = mapper.writeValueAsString(exception.elements)
        )

        val exceptionTable = exceptionTableCache.get(exceptionTableCacheKey) ?: return

        val newExceptionEventTable = ExceptionEventTable(
            exceptionId = exceptionTable.id,
            message = exception.message
        )

        exceptionEventRepository.save(newExceptionEventTable)

        if (!exceptionTable.ignore) {
            transport.send(event)
        }
    }
}

private data class ExceptionTableCacheKey(
    val appId: Long,
    val className: String,
    val elements: String,
)

private suspend fun ExceptionRepository.getOrCreate(key: ExceptionTableCacheKey): ExceptionTable {
    val (appId, className, elements) = key

    val existingTable = findAllByAppIdAndClassNameAndHashCode(appId, className, elements.hashCode())
        .firstOrNull { it.elements == key.elements }

    return existingTable ?: save(ExceptionTable(appId = key.appId, className = key.className, elements = key.elements))
}
