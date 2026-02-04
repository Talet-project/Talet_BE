package com.talet.talet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookshelfDTO {
    @Schema(description = "책 ID", example = "UUID-UUID-BOOK-0001")
    private String bookId;
    @Schema(description = "책 이름", example = "토끼와 거북이")
    private String bookName;
    @Schema(description = "썸네일 주소", example = "https://talet.site/썸네일1.jpg")
    private String thumbnail;
    @Schema(description = "책 전체 페이지", example = "6")
    private int totalPage;
    @Schema(description = "현재 읽고있는 페이지", example = "2")
    private Integer currentPage; // null 하지말고 0으로
    @Schema(description = "북마크 상태", example = "false")
    private boolean isLiked;

    public int getCurrentPage() {
        return currentPage == null ? 0 : currentPage;
    }
}
