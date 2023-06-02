package io.wafflestudio.truffle.core.protocol

import io.wafflestudio.truffle.core.store.r2dbc.ExceptionTable.Element

data class TruffleException(
    val className: String,
    val message: String?,
    val elements: List<Element>,
)
