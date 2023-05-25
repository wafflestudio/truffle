package io.wafflestudio.truffle.core.store.r2dbc

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ExceptionRepository : CoroutineCrudRepository<ExceptionTable, Long> {
    suspend fun countAllByAppId(appId: Long): Long
    suspend fun countAllByAppIdAndStatus(appId: Long, status: Int?): Long
    fun findAllByAppIdAndClassNameAndHashCode(appId: Long, className: String, hashCode: Int): Flow<ExceptionTable>
}
