package com.talet.talet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MemberProfileImageRequestDTO {
    @Schema(type = "string", format = "binary", description = "프로필 이미지 파일")
    private MultipartFile profile;
}
