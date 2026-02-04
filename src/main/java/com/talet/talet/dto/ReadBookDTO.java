package com.talet.talet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ReadBookDTO {
    @Schema(description = "책 ID", example = "UUID-0001-0001-0001")
    private String bookId;
    @Schema(description = "현재 읽은 페이지", example = "3")
    private int currentPage;
}
