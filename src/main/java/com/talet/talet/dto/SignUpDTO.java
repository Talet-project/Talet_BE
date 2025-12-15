package com.talet.talet.dto;

import com.talet.talet.util.LanguageEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "회원가입 요청")
public class SignUpDTO {
    @Schema(description = "사용자 닉네임", example = "홍길동")
    private String name;
    @Schema(description = "사용자 생년월", example = "2015-01")
    private String birthDate;
    @Schema(description = "성별", example = "남성")
    private String gender;
    @Schema(description = "사용 언어", example = "[\"KOREAN\", \"ENGLISH\"]")
    private List<LanguageEnum> nativeLanguages;
    @Schema(description = "넣지마세요")
    private String platform;
    @Schema(description = "넣지 마세요 제가 알아서 바꿉니다.")
    private String identifier;
}
