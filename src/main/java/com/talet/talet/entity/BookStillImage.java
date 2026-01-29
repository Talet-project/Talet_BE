package com.talet.talet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "book_still_image")
@Getter
@Setter
public class BookStillImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private FairyTaleBook book;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;
}
