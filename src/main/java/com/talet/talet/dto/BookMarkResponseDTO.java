package com.talet.talet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "북마크 데이터")
public class BookMarkResponseDTO {
    @Schema(description = "책 UUID", example = "UUID-0001-0001-0001")
    private String bookId;
    @Schema(description = "책 제목", example = "토끼와 거북이")
    private String title;
    @Schema(description = "책 썸네일 이미지 URL", example = "https://talet.site/images/book_thumbnail.jpg")
    private String thumbnail;
}
