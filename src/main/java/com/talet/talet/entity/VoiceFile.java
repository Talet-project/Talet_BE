package com.talet.talet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "voice_file")
@Getter
@Setter
public class VoiceFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;      // 원본 파일명
    private String filePath;      // 파일 저장경로/URL
    private String profile;       // 프로필

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // member 테이블의 PK를 FK로 사용
    private Member member;
}
