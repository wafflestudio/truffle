package io.wafflestudio.truffle.api.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.wafflestudio.truffle.api.AuthRequest
import io.wafflestudio.truffle.api.AuthResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod

@RouterOperations(
    RouterOperation(
        path = "/api/v1/auth",
        method = [RequestMethod.POST],
        operation = Operation(
            tags = ["API:Auth"],
            operationId = "API:Auth",
            security = [SecurityRequirement(name = "bearer-key")],
            summary = "액세스 토큰 발급",
            requestBody = RequestBody(
                required = true,
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = AuthRequest::class)
                    )
                ]
            ),
            responses = [ApiResponse(content = [Content(schema = Schema(implementation = AuthResponse::class))])]
        )
    )
)
annotation class AuthDocs
