package com.talet.talet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "구글 로그인 응답")
public class TokenResponse {
    @Schema(description = "액세스 토큰(JWT)", example = "eyJhbGciOiJIUzI1NiJ9...", nullable = true)
    private String accessToken;
    @Schema(description = "리프레시 토큰(JWT)", example = "eyJhbGciOiJIUzI1NiJ9...", nullable = true)
    private String refreshToken;
    @Schema(description = "회원가입 진행 시 발급되는 추가 토큰", example = "SIGNUP-1234", nullable = true)
    private String signUpToken;

    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public TokenResponse(String signUpToken) {
        this.signUpToken = signUpToken;
    }
}
