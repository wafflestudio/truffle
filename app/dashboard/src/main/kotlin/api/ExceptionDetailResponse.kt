package io.wafflestudio.truffle.api

import com.querydsl.core.annotations.QueryProjection
import io.wafflestudio.truffle.core.store.r2dbc.ExceptionTable.Element
import java.time.Instant

data class ExceptionDetailResponse @QueryProjection constructor(
    val id: Long,
    val className: String,
    val message: String?,
    val elements: List<Element>,
    val createdAt: Instant,
    val lastEventAt: Instant,
    val eventCnt: Long,
)
