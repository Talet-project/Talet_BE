package com.talet.talet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Entity
@Table(name = "reading_book")
@Getter
@Setter
public class ReadingBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private FairyTaleBook book;

    @Column(nullable = false)
    private int status; // Double로 하고

    @Column(nullable = false)
    private boolean isCompleted; // 이건 굳이 무쓸모 - iOS 에서 알아서 처리할거임
}
