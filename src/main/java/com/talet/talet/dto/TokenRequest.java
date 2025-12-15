package com.talet.talet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "구글 로그인 요청")
public class TokenRequest {
    @Schema(description = "구글 ID 토큰", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjE2...")
    private String idToken;
}
