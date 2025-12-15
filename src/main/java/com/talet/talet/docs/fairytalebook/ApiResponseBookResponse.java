package com.talet.talet.docs.fairytalebook;

import com.talet.talet.dto.BookResponseDTO;
import com.talet.talet.util.TaletApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

public class ApiResponseBookResponse extends TaletApiResponse<BookResponseDTO> {
    public ApiResponseBookResponse(boolean success, String message, BookResponseDTO data, ErrorResponse error) {
        super(success, message, data, error);
    }
}
