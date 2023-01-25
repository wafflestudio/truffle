package io.wafflestudio.truffle.core

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.wafflestudio.truffle.core.TruffleEvent.V1
import io.wafflestudio.truffle.core.protocol.TruffleApp
import io.wafflestudio.truffle.core.protocol.TruffleException
import io.wafflestudio.truffle.core.protocol.TruffleRuntime
import io.wafflestudio.truffle.core.protocol.TruffleVersion

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "version")
@JsonSubTypes(JsonSubTypes.Type(value = V1::class, name = TruffleVersion.V1))
interface TruffleEvent {
    val version: String
    var client: TruffleClient?

    data class V1(
        val app: TruffleApp,
        val runtime: TruffleRuntime,
        val exception: TruffleException,
        val description: String? = null,
        override val version: String = TruffleVersion.V1,
    ) : TruffleEvent {
        override var client: TruffleClient? = null // FIXME
    }
}
