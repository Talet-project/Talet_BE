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
    private int currentPage;


}
