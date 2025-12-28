package com.talet.talet.dto;

import com.talet.talet.util.LanguageEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "보이스 저장 요청")
public class VoiceRequestDTO {
    @Schema(description = "보이스 파일 이름", example = "아빠 목소리")
    private String fileName;
    @Schema(description = "언어", example = "ENGLISH")
    private LanguageEnum language;
}
