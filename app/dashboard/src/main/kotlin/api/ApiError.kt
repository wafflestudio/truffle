package io.wafflestudio.truffle.api

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

enum class ApiError(private val status: HttpStatus) {
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    FORBIDDEN(HttpStatus.FORBIDDEN);

    val exception: ResponseStatusException get() = ResponseStatusException(status)
}
