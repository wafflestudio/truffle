package io.wafflestudio.truffle.api

import io.wafflestudio.truffle.core.store.r2dbc.ExceptionStatus

data class UpdateExceptionRequest(
    val status: ExceptionStatus,
)
