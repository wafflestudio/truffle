package io.wafflestudio.truffle.core.store.r2dbc

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AppRepository : CoroutineCrudRepository<AppTable, Long> {
    suspend fun findByApiKey(apiKey: String): AppTable?
}
