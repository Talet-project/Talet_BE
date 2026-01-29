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
    private Integer currentPage;
    private boolean isLiked;
}
