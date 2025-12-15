package com.talet.talet.docs;

import com.talet.talet.util.TaletApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiResponseMessage")
public class ApiResponseMessage extends TaletApiResponse<Object> {
    public ApiResponseMessage(boolean success, String message, Object data, ErrorResponse error) {
        super(success, message, data, error);
    }
}
