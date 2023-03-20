package io.wafflestudio.truffle.core.store.r2dbc

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("exceptions")
data class ExceptionTable(
    @Id
    val id: Long = 0L,
    val appId: Long,
    val className: String,
    val elements: String,
    val hashCode: Int = elements.hashCode(),
    val ignore: Boolean = false,
)
