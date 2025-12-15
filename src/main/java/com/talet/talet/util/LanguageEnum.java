package com.talet.talet.util;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용 언어")
public enum LanguageEnum {
    @Schema(description = "한국어")
    KOREAN,
    @Schema(description = "영어")
    ENGLISH,
    @Schema(description = "일본어")
    JAPANESE,
    @Schema(description = "증국어")
    CHINESE,
    @Schema(description = "베트남어")
    VIETNAMESE,
    @Schema(description = "태국어")
    THAI
}
// 한국어, 영어, 일본어, 중국어, 베트남어, 태국어