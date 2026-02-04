package com.talet.talet.docs.member;

import com.talet.talet.dto.BookshelfDTO;
import com.talet.talet.util.TaletApiResponse;

public class ApiResponseBookshelfResponse extends TaletApiResponse<BookshelfDTO> {

    public ApiResponseBookshelfResponse(boolean success, String message, BookshelfDTO data, ErrorResponse error) {
        super(success, message, data, error);
    }
}
