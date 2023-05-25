package io.wafflestudio.truffle.handler

import io.wafflestudio.truffle.api.AuthRequest
import io.wafflestudio.truffle.api.AuthResponse
import io.wafflestudio.truffle.api.filter.ApiSecurityFilter
import io.wafflestudio.truffle.core.store.r2dbc.AppRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Controller
class AuthHandler(
    private val appRepository: AppRepository,
    private val security: ApiSecurityFilter
) {

    suspend fun login(request: ServerRequest): ServerResponse {
        val apiKey = request.awaitBody<AuthRequest>().apiKey
        val app = appRepository.findByApiKey(apiKey)

        return if (app != null) {
            ServerResponse
                .ok()
                .bodyValueAndAwait(AuthResponse(accessToken = security.generateToken(app.id)))
        } else {
            ServerResponse
                .status(HttpStatus.UNAUTHORIZED)
                .bodyValueAndAwait(Unit)
        }
    }
}
