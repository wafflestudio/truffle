package io.wafflestudio.truffle.core.store.r2dbc

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("apps")
data class AppTable(
    @Id
    val id: Long = 0L,
    val name: String,
    val phase: String?,
    val apiKey: String,
    val slackChannel: String?,
)
