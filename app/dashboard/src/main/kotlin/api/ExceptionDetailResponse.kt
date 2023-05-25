package io.wafflestudio.truffle.api

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.wafflestudio.truffle.core.protocol.TruffleException
import java.time.Instant

data class ExceptionDetailResponse(
    val id: Long,
    val className: String,
    val message: String?,
    @JsonIgnore
    val elementStr: String,
    val createdAt: Instant,
    val lastEventAt: Instant,
    val eventCnt: Long,
) {
    val elements: List<TruffleException.Element> = mapper.readValue(elementStr)
}

private val mapper = jacksonObjectMapper()
