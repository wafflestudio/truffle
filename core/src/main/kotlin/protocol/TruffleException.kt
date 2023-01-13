package io.wafflestudio.truffle.core.protocol

data class TruffleException(
    val className: String,
    val message: String?,
    val elements: List<Element>,
) {
    data class Element(
        val className: String,
        val methodName: String,
        val lineNumber: Int,
        val fileName: String,
        val isInAppInclude: Boolean,
    )
}
