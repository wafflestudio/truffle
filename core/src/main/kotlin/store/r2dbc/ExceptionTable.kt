package io.wafflestudio.truffle.core.store.r2dbc

import io.wafflestudio.truffle.core.store.r2dbc.ExceptionStatus.TRACKING
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("exceptions")
data class ExceptionTable(
    @Id
    val id: Long = 0L,
    val appId: Long,
    val className: String,
    val message: String?,
    val elements: String,
    val hashCode: Int = elements.hashCode(),
    val status: Int = TRACKING.value,
    val createdAt: Instant = Instant.now(),
)

enum class ExceptionStatus(val value: Int) {
    TRACKING(0), RESOLVED(1), IGNORED(2)
}
