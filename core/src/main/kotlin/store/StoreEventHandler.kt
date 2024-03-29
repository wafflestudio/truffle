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
import io.wafflestudio.truffle.core.store.r2dbc.ExceptionStatus
import io.wafflestudio.truffle.core.store.r2dbc.ExceptionTable
import io.wafflestudio.truffle.core.store.r2dbc.ExceptionTable.Element
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
        ttl = Duration.ofMinutes(1),
        loader = CacheLoader.SingleLoader { key -> exceptionRepository.get(key) }
    )

    override suspend fun handle(event: TruffleEvent) = when (event) {
        is TruffleEvent.V1 -> handleV1(event)
        else -> error("not reachable")
    }

    private suspend fun handleV1(event: TruffleEvent.V1) {
        val client = checkNotNull(event.client)
        val exception = event.exception

        val cacheKey = ExceptionTableCacheKey(
            appId = client.id,
            className = exception.className,
            elements = exception.elements
        )

        val exceptionTable = exceptionTableCache.get(cacheKey) ?: mutex.withLock {
            exceptionTableCache.get(cacheKey) ?: run {
                exceptionTableCache.evict(cacheKey)

                exceptionRepository.save(
                    ExceptionTable(
                        appId = cacheKey.appId,
                        className = cacheKey.className,
                        elements = cacheKey.elements,
                        hashCode = cacheKey.elements.hashValue,
                        message = exception.message
                    )
                )
            }
        }

        val newExceptionEventTable = ExceptionEventTable(
            exceptionId = exceptionTable.id,
            message = exception.message
        )

        exceptionEventRepository.save(newExceptionEventTable)

        // FIXME: 대시보드에서 변경한 상태가 캐시 만료 후에야 반영되는 이슈
        if (exceptionTable.status == ExceptionStatus.RESOLVED.value) {
            exceptionRepository.save(exceptionTable.copy(status = ExceptionStatus.TRACKING.value))
        }

        if (exceptionTable.status != ExceptionStatus.IGNORED.value) {
            transport.send(event)
        }
    }

    private suspend fun ExceptionRepository.get(key: ExceptionTableCacheKey): ExceptionTable? =
        findAllByAppIdAndClassNameAndHashCode(
            key.appId,
            key.className,
            key.elements.hashValue
        )
            .firstOrNull { it.elements == key.elements }

    private data class ExceptionTableCacheKey(
        val appId: Long,
        val className: String,
        val elements: List<Element>,
    )

    private val List<Element>.hashValue: Int
        get() = mapper.writeValueAsString(this).hashCode()
}
