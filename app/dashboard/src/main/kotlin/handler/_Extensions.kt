package io.wafflestudio.truffle.handler

import io.wafflestudio.truffle.api.ApiError.BAD_REQUEST
import org.springframework.web.reactive.function.server.ServerRequest

fun ServerRequest.pathLong(name: String) = runCatching {
    pathVariable(name).toLong()
}.getOrElse {
    throw BAD_REQUEST.exception
}

fun ServerRequest.paramLong(name: String) = runCatching {
    queryParam(name).get().toLong()
}.getOrElse {
    throw BAD_REQUEST.exception
}
