package io.wafflestudio.truffle.api

import com.querydsl.core.annotations.QueryProjection
import java.time.Instant

data class ExceptionListResponse @QueryProjection constructor(
    val content: List<ExceptionBriefResponse>,
    val hasNext: Boolean,
    val totalCnt: Long,
) {
    data class ExceptionBriefResponse(
        val id: Long,
        val className: String,
        val message: String?,
        val createdAt: Instant,
        val lastEventAt: Instant,
        val eventCnt: Long,
    )
}
