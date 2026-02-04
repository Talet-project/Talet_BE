package com.talet.talet.docs.auth;

import com.talet.talet.docs.ApiResponseError;
import com.talet.talet.docs.SwaggerErrorExamples;
import com.talet.talet.docs.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "토큰 재발급",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponseTokenResponse.class),
                        examples = @ExampleObject(name = "토큰 재발급 성공", value = SwaggerExamples.TOKEN_REFRESH_OK)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponseError.class),
                        examples = @ExampleObject(name = "잘못된 요청", value = SwaggerErrorExamples.COMMON_INVALID_REQUEST)
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "서버 오류",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponseError.class),
                        examples = @ExampleObject(name = "서버 오류", value = SwaggerErrorExamples.COMMON_INTERNAL_ERROR)
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "유효하지 않은 토큰 혹은 토큰 만료",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponseError.class),
                        examples = {
                                @ExampleObject(name = "유효하지 않은 토큰", value = SwaggerErrorExamples.AUTH_TOKEN_INVALID),
                                @ExampleObject(name = "토큰 만료", value = SwaggerErrorExamples.AUTH_TOKEN_EXPIRED)
                        }
                )
        )
})

public @interface AuthRefresh {
}
