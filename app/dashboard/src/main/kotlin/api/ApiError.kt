package io.wafflestudio.truffle.api

sealed class ApiError : RuntimeException() {
    abstract val status: Int

    class UnAuthorized(override val status: Int = 401) : ApiError()
    class Forbidden(override val status: Int = 403) : ApiError()
    class BadRequest(override val status: Int = 400) : ApiError()
    class NotFound(override val status: Int = 404) : ApiError()
}
