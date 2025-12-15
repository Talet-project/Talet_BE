package com.talet.talet.dto;

import com.talet.talet.util.LanguageEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema(description = "사용자 정보 요청")
public class MemberRequestDTO {
    @Schema(description = "사용자 닉네임", example = "홍길동")
    private String nickname;
    @Schema(description = "사용자 성별", example = "남성")
    private String gender;
    @Schema(description = "생년월", example = "2019-01")
    private String birthday;
    @Schema(description = "사용 언어", example = "[\"KOREAN\", \"ENGLISH\"]")
    private List<LanguageEnum>  languages;
}
