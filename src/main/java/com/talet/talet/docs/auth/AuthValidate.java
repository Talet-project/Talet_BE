package com.talet.talet.docs.auth;

import com.talet.talet.docs.ApiResponseError;
import com.talet.talet.docs.SwaggerErrorExamples;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(
                responseCode = "204",
                description = "토큰 유효"
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

public @interface AuthValidate {
}
