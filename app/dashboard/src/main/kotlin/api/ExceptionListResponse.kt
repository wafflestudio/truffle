package io.wafflestudio.truffle.api

import java.time.Instant

data class ExceptionListResponse(
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
