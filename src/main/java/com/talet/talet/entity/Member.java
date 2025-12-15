package com.talet.talet.entity;

import com.talet.talet.util.LanguageEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String memberId; // 유저 PK

    @Column(nullable = true, length = 50)
    private String identifier; // 애플 로그인 구현 시 필수 항목 - 이메일을 제공하지 않고 sub 로 고유 id 값을 제공

    @Column(nullable = false, length = 20)
    private String name; // 이름(닉네임)

    @Column(nullable = false, length = 10) // 2025-01-01
    private String birthDate; // 생년월일

    @Column(nullable = false, length = 10) // man, girl
    private String gender; // 성별

    @ElementCollection(targetClass = LanguageEnum.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_languages", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "language")
    private List<LanguageEnum> nativeLanguages; // 모국어

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoiceFile> voiceFiles = new ArrayList<>(); // 목소리 저장

    @Column(nullable = false, length = 50)
    private String platform; // 소셜로그인 플랫폼

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookMark> bookmarks; // 찜 목록

    @Column(nullable = false, length = 10)
    private String role; // Role_name;

    @Column(nullable = true, length = 255)
    private String profileImage;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadingBook>  readingBooks = new ArrayList<>(); // 현재 읽고 있는 책
}
