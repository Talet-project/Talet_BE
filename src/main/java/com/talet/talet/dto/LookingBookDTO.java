package com.talet.talet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Schema(description = "책 둘러보기 데이터")
public class LookingBookDTO {
    @Schema(description = "책 UUID", example = "UUID-0001-0001-0001")
    private String id;
    @Schema(description = "책 제목", example = "토끼와 거북이")
    private String name;
    @Schema(description = "책 썸네일 이미지 URL", example = "https://talet.site/images/book_thumbnail.jpg")
    private String thumbnail;
    @Schema(description = "책 태그", example = "[\"지혜\", \"선과 악\", \"나눔\"]")
    private List<String> tags;
    @Schema(description = "책의 짧은 줄거리", example = "{\"ko\": \"토끼와 거북이의 경주 이야기\", \"en\": \"A race between a rabbit and a turtle.\"}")
    private Map<String, String> shorts;
    @Schema(description = "사용자 북마크 여부", example = "true")
    private boolean bookmark;
}
