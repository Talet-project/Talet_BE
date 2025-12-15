package com.talet.talet.docs.auth;

import com.talet.talet.docs.ApiResponseError;
import com.talet.talet.docs.ApiResponseMessage;
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

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "회원 탈퇴 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponseMessage.class),
                        examples = @ExampleObject(name = "회원 탈퇴 성공", value = SwaggerExamples.DELETE_OK)
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
        )
})

public @interface AuthDelete {
}
