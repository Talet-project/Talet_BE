package com.talet.talet.dto;

import com.talet.talet.util.LanguageEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoiceResponseDTO {
    private Long id;
    private String fileName;
    private String filePath;
    private String profile;
    private LanguageEnum language;
}
