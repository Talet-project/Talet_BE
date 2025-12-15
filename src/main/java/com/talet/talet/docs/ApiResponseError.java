package com.talet.talet.docs;

import com.talet.talet.util.TaletApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiResponseError")
public class ApiResponseError extends TaletApiResponse<TaletApiResponse.ErrorResponse> {

    public ApiResponseError(boolean success, String message, ErrorResponse data, ErrorResponse error) {
        super(success, message, data, error);
    }
}