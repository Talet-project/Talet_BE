package com.talet.talet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookshelfDTO {
    private String bookId;
    private String bookName;
    private String thumbnail;
    private int totalPage;
    private Integer currentPage; // null 하지말고 0으로
    private boolean isLiked;

    public int getCurrentPage() {
        return currentPage == null ? 0 : currentPage;
    }
}
