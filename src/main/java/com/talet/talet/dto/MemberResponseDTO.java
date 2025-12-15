package com.talet.talet.dto;

import com.talet.talet.util.LanguageEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@Schema(description = "사용자 정보 응답")
public class MemberResponseDTO {
    @Schema(description = "사용자 프로필 이미지 URL", example = "https://talet.site/images/profile1.jpg")
    private String profileImage;
    @Schema(description = "사용자 닉네임", example = "홍길동")
    private String nickname;
    @Schema(description = "사용자 성별", example = "남성")
    private String gender;
    @Schema(description = "생년월", example = "2019-01")
    private String birthday;
    @Schema(description = "사용 언어", example = "[\"KOREAN\", \"ENGLISH\"]")
    private List<LanguageEnum> languages;
}
