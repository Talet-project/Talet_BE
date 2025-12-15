package com.talet.talet.docs.fairytalebook;

import com.talet.talet.dto.LookingBookDTO;
import com.talet.talet.util.TaletApiResponse;

public class ApiResponseLookingBookResponse extends TaletApiResponse<LookingBookDTO> {
    public ApiResponseLookingBookResponse(boolean success, String message, LookingBookDTO data, ErrorResponse error) {
        super(success, message, data, error);
    }
}
