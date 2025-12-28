package com.talet.talet.docs.voice;

import com.talet.talet.dto.VoiceResponseDTO;
import com.talet.talet.util.TaletApiResponse;

public class ApiResponseVoiceResponse extends TaletApiResponse<VoiceResponseDTO> {
    public ApiResponseVoiceResponse(boolean success, String message, VoiceResponseDTO data, ErrorResponse error) {
        super(success, message, data, error);
    }
}
