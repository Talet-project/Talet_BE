package com.talet.talet.docs.member;

import com.talet.talet.dto.MemberResponseDTO;
import com.talet.talet.util.TaletApiResponse;

public class ApiResponseMemberResponse extends TaletApiResponse<MemberResponseDTO> {
    public ApiResponseMemberResponse(boolean success, String message, MemberResponseDTO data, ErrorResponse error) {
        super(success, message, data, error);
    }
}
