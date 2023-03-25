package io.wafflestudio.truffle.core.store.r2dbc

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ExceptionEventRepository : CoroutineCrudRepository<ExceptionEventTable, Long>
