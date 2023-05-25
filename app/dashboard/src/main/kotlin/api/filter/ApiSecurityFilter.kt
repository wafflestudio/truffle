package io.wafflestudio.truffle.api.filter

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.wafflestudio.truffle.api.ApiError
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import javax.crypto.SecretKey

@Component
class ApiSecurityFilter : HandlerFilterFunction<ServerResponse, ServerResponse> {

    companion object {
        private val key: SecretKey = Keys.hmacShaKeyFor("truffleistrufflewafflestudioiswafflestudio".toByteArray())
        private val parser: JwtParser = Jwts.parserBuilder().setSigningKey(key).build()
        const val APP_ID = "APP_ID"
    }

    override fun filter(request: ServerRequest, next: HandlerFunction<ServerResponse>): Mono<ServerResponse> {
        val token = runCatching {
            request.headers().firstHeader(HttpHeaders.AUTHORIZATION)!!.split(" ")[1]
        }.getOrElse {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue(Unit)
        }

        if (!validateToken(token)) {
            return ServerResponse
                .status(HttpStatus.UNAUTHORIZED)
                .bodyValue(Unit)
        }

        request.attributes()[APP_ID] = getAppId(token)

        return next.handle(request)
    }

    fun generateToken(appId: Long): String {
        return Jwts.builder()
            .setSubject("$appId")
            .setIssuedAt(Date())
            .setExpiration(Date.from(Instant.now().plus(3, ChronoUnit.HOURS)))
            .signWith(key)
            .compact()
    }

    private fun validateToken(token: String): Boolean {
        return runCatching {
            parser.parseClaimsJws(token).body.expiration > Date()
        }.getOrElse {
            if (it is ExpiredJwtException) {
                false
            } else {
                throw it
            }
        }
    }

    private fun getAppId(token: String): Long {
        return parser.parseClaimsJws(token).body.subject.toLong()
    }
}

val ServerRequest.appId: Long
    get() = runCatching {
        attributes()[ApiSecurityFilter.APP_ID] as Long
    }.getOrElse {
        throw ApiError.UnAuthorized()
    }
