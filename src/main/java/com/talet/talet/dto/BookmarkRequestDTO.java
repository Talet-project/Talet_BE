package com.talet.talet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class BookmarkRequestDTO {
    @Schema(description = "책 ID", example = "UUID-UUID-BOOK-0001")
    private String bookId;
}
