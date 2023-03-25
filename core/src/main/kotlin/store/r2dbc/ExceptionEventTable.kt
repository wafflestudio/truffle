package io.wafflestudio.truffle.core.store.r2dbc

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("exception_events")
data class ExceptionEventTable(
    @Id
    val id: Long = 0L,
    val exceptionId: Long,
    val message: String?,
    val createdAt: Instant = Instant.now(),
)
