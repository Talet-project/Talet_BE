package com.talet.talet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Schema(description = "책 상세정보 데이터")
public class BookDetailDTO {
    @Schema(description = "책 UUID", example = "UUID-0001-0001-0001")
    private String id;
    @Schema(description = "책 제목", example = "토끼와 거북이")
    private String name;
    @Schema(description = "책 썸네일 이미지 URL", example = "https://talet.site/images/book_thumbnail.jpg")
    private String thumbnail;
    @Schema(description = "책 관련 이미지 리스트", example = "[\"https://talet.site/images/stillcut1.jpg\", \"https://talet.site/images/stillcut2.jpg\"]")
    private List<String> stillImages;
    @Schema(description = "책 태그", example = "[\"지혜\", \"선과 악\", \"나눔\"]")
    private List<String> tags;
    @Schema(description = "책의 짧은 줄거리", example = "{\"ko\": \"토끼와 거북이의 경주 이야기\", \"en\": \"A race between a rabbit and a turtle.\"}")
    private Map<String, String> shorts;
    @Schema(description = "책의 전체 줄거리", example = "{\"ko\": \"느림보 거북이가 결국 토끼를 이기는 이야기입니다.\", \"en\": \"A slow turtle eventually wins against the overconfident rabbit.\"}")
    private Map<String, String> plots;
    @Schema(description = "사용자 북마크 여부", example = "true")
    private boolean bookmark;
}
