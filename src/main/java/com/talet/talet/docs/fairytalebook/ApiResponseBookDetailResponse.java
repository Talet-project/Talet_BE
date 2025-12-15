package com.talet.talet.docs.fairytalebook;

import com.talet.talet.dto.BookDetailDTO;
import com.talet.talet.util.TaletApiResponse;

public class ApiResponseBookDetailResponse extends TaletApiResponse<BookDetailDTO> {

    public ApiResponseBookDetailResponse(boolean success, String message, BookDetailDTO data, ErrorResponse error) {
        super(success, message, data, error);
    }
}
