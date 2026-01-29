package com.talet.talet.entity;

import com.talet.talet.util.BookTag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fairy_tale_book")
@Getter
@Setter
public class FairyTaleBook {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id; // 책 PK

    @Column(nullable = false)
    private String name; // 책 제목

    @Column(nullable = false)
    private String englishName; // 책 영어 제목(폴더 명)

    @Column(nullable = false)
    private String thumbnail; // 책 썸네일

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<BookStillImage> stillImages = new ArrayList<>(); // 책 스틸컷

    @ElementCollection(targetClass = BookTag.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "book_tag",
            joinColumns = @JoinColumn(name = "book_id")
    )
    @Column(name = "tag")
    private List<BookTag> tags = new ArrayList<>(); // 책 태그

    @Column(nullable = false)
    private int count; // 책 클릭 수

    // 책 내용을 파일로 저장할지, 데이터베이스에 저장할지 고민중
    @Column(nullable = false)
    private String filePath; // 책 파일 위치

    @Column(nullable = false)
    private int totalPage; // 책 줄 개수(문단 말고 한줄 한줄로 저장) <-- 이걸 페이지로 변경

    //책이 삭제될 때 찜 목록도 전체 다 삭제되도록 설정
    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BookMark> bookmarks;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadingBook> readingBooks;
}
