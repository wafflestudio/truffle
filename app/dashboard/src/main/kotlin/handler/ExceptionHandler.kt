package io.wafflestudio.truffle.handler

import com.querydsl.core.types.Projections
import io.wafflestudio.truffle.api.ApiError.Forbidden
import io.wafflestudio.truffle.api.ApiError.NotFound
import io.wafflestudio.truffle.api.ExceptionDetailResponse
import io.wafflestudio.truffle.api.ExceptionListResponse
import io.wafflestudio.truffle.api.ExceptionListResponse.ExceptionBriefResponse
import io.wafflestudio.truffle.api.UpdateExceptionRequest
import io.wafflestudio.truffle.api.filter.appId
import io.wafflestudio.truffle.core.store.r2dbc.ExceptionRepository
import io.wafflestudio.truffle.core.store.r2dbc.ExceptionStatus
import io.wafflestudio.truffle.core.store.r2dbc.QExceptionEventTable.exceptionEventTable
import io.wafflestudio.truffle.core.store.r2dbc.QExceptionTable.exceptionTable
import io.wafflestudio.truffle.core.store.r2dbc.QueryDslRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Controller
class ExceptionHandler(
    private val exceptionRepository: ExceptionRepository,
    private val exceptionQueryDsl: QueryDslRepository,
) {

    suspend fun gets(request: ServerRequest): ServerResponse {
        val appId: Long = request.appId
        val page: Long = request.paramLong("page")
        val size: Long = request.paramLong("size")
        val status: Int? = runCatching { ExceptionStatus.valueOf(request.queryParam("status").get()).value }
            .getOrNull()

        val content = exceptionQueryDsl.gets(appId = appId, status = status, page = page, size = size)
        val totalCnt = if (status != null) {
            exceptionRepository.countAllByAppIdAndStatus(appId, status)
        } else {
            exceptionRepository.countAllByAppId(appId)
        }

        val response = ExceptionListResponse(
            content = content,
            hasNext = (page + 1) * size < totalCnt,
            totalCnt = totalCnt
        )

        return ServerResponse.ok().bodyValueAndAwait(response)
    }

    suspend fun get(request: ServerRequest): ServerResponse {
        val appId = request.appId
        val exceptionId = request.pathLong("id")

        val response = exceptionQueryDsl.get(appId = appId, exceptionId = exceptionId) ?: throw NotFound()

        return ServerResponse.ok().bodyValueAndAwait(response)
    }

    suspend fun update(request: ServerRequest): ServerResponse {
        val appId = request.appId
        val exceptionId = request.pathLong("id")
        val newStatus = request.awaitBody<UpdateExceptionRequest>().status

        val exceptionTable = exceptionRepository.findById(exceptionId) ?: throw NotFound()

        if (exceptionTable.appId != appId) {
            throw Forbidden()
        }

        exceptionRepository.save(exceptionTable.copy(status = newStatus.value))

        return ServerResponse.ok().bodyValueAndAwait(Unit)
    }
}

private suspend fun QueryDslRepository.gets(
    appId: Long,
    status: Int?,
    page: Long,
    size: Long,
): List<ExceptionBriefResponse> {
    return query { builder ->
        builder.select(
            Projections.constructor(
                ExceptionBriefResponse::class.java,
                exceptionTable.id,
                exceptionTable.className,
                exceptionTable.message,
                exceptionTable.createdAt,
                exceptionEventTable.createdAt.max().`as`("last_event_at"),
                exceptionEventTable.id.count().`as`("event_cnt"),
            )
        )
            .from(exceptionEventTable)
            .innerJoin(exceptionTable)
            .on(exceptionEventTable.exceptionId.eq(exceptionTable.id))
            .where(
                exceptionTable.appId.eq(appId)
                    .and(status?.let { exceptionTable.status.eq(it) })
            )
            .groupBy(exceptionTable.id, exceptionTable.className, exceptionTable.message, exceptionTable.createdAt)
            .orderBy(exceptionEventTable.createdAt.`as`("last_event_at").desc())
            .offset(page * size)
            .limit(size)
    }
        .all()
        .asFlow()
        .toList()
}

private suspend fun QueryDslRepository.get(appId: Long, exceptionId: Long): ExceptionDetailResponse? {
    return query { builder ->
        builder.select(
            Projections.constructor(
                ExceptionDetailResponse::class.java,
                exceptionTable.id,
                exceptionTable.className,
                exceptionEventTable.message,
                exceptionTable.elements.`as`("element_str"),
                exceptionTable.createdAt,
                exceptionEventTable.createdAt.max().`as`("last_event_at"),
                exceptionEventTable.id.count().`as`("event_cnt"),
            )
        )
            .from(exceptionEventTable)
            .innerJoin(exceptionTable)
            .on(exceptionEventTable.exceptionId.eq(exceptionTable.id))
            .where(
                exceptionTable.appId.eq(appId)
                    .and(exceptionTable.id.eq(exceptionId))
            )
            .groupBy(exceptionEventTable.exceptionId)
    }
        .awaitOneOrNull()
}
