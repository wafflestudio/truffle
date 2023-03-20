package io.wafflestudio.truffle.core.store.r2dbc

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ExceptionRepository : CoroutineCrudRepository<ExceptionTable, Long> {
    suspend fun findAllByAppIdAndClassNameAndHashCode(
        appId: Long,
        className: String,
        hashCode: Int,
    ): Flow<ExceptionTable>
}
