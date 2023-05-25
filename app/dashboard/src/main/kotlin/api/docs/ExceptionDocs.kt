package io.wafflestudio.truffle.api.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.wafflestudio.truffle.api.ExceptionDetailResponse
import io.wafflestudio.truffle.api.ExceptionListResponse
import io.wafflestudio.truffle.api.UpdateExceptionRequest
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod

@RouterOperations(
    RouterOperation(
        path = "/api/v1/exceptions",
        method = [RequestMethod.GET],
        operation = Operation(
            tags = ["API:Exceptions"],
            operationId = "API:Exceptions:list",
            security = [SecurityRequirement(name = "bearer-key")],
            summary = "이슈 목록",
            parameters = [
                Parameter(
                    name = "status",
                    `in` = ParameterIn.QUERY,
                    required = false,
                    schema = Schema(
                        type = "string",
                        example = "TRACKING",
                        allowableValues = ["TRACKING", "RESOLVED", "IGNORED"]
                    )
                ),
                Parameter(
                    name = "page",
                    `in` = ParameterIn.QUERY,
                    required = true,
                    schema = Schema(type = "integer", example = "0")
                ),
                Parameter(
                    name = "size",
                    `in` = ParameterIn.QUERY,
                    required = true,
                    schema = Schema(type = "integer", example = "20")
                ),
            ],
            responses = [ApiResponse(content = [Content(schema = Schema(implementation = ExceptionListResponse::class))])]
        )
    ),
    RouterOperation(
        path = "/api/v1/exceptions/{id}",
        method = [RequestMethod.GET],
        operation = Operation(
            tags = ["API:Exceptions"],
            operationId = "API:Exceptions:Detail",
            security = [SecurityRequirement(name = "bearer-key")],
            summary = "이슈 상세",
            parameters = [
                Parameter(
                    name = "id",
                    `in` = ParameterIn.PATH,
                    required = true,
                    schema = Schema(type = "integer", example = "1")
                ),
            ],
            responses = [ApiResponse(content = [Content(schema = Schema(implementation = ExceptionDetailResponse::class))])]
        )
    ),
    RouterOperation(
        path = "/api/v1/exceptions/{id}",
        method = [RequestMethod.PATCH],
        operation = Operation(
            tags = ["API:Exceptions"],
            operationId = "API:Exceptions:Update",
            security = [SecurityRequirement(name = "bearer-key")],
            summary = "이슈 업데이트",
            parameters = [
                Parameter(
                    name = "id",
                    `in` = ParameterIn.PATH,
                    required = true,
                    schema = Schema(type = "integer", example = "1")
                ),
            ],
            requestBody = RequestBody(
                required = true,
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = UpdateExceptionRequest::class)
                    )
                ]
            ),
        )
    )
)
annotation class ExceptionDocs
