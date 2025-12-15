package com.talet.talet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@Schema(description = "책 검색정보 데이터")
public class BookResponseDTO {
    @Schema(description = "책 UUID", example = "UUID-0001-0001-0001")
    private String id;
    @Schema(description = "책 제목", example = "토끼와 거북이")
    private String name;
    @Schema(description = "책 썸네일 이미지 URL", example = "https://talet.site/images/book_thumbnail.jpg")
    private String thumbnail;
    @Schema(description = "책 태그", example = "[\"지혜\", \"선과 악\", \"나눔\"]")
    private List<String> tags;
    @Schema(description = "책의 전체 줄거리", example = "느림보 거북이가 결국 토끼를 이기는 이야기입니다.")
    private String plot;
}
