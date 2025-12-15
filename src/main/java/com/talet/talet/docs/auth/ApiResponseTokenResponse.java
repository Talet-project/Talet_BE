package com.talet.talet.docs.auth;

import com.talet.talet.dto.TokenResponse;
import com.talet.talet.util.TaletApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiResponseTokenResponse")
public class ApiResponseTokenResponse extends TaletApiResponse<TokenResponse> {
    public ApiResponseTokenResponse(boolean success, String message, TokenResponse data, ErrorResponse error) {
        super(success, message, data, error);
    }
}
