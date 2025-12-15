package com.talet.talet.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TTSJob {
    private String jobId;
    private String bookId;
    private String memberId;
    private String language;
    private Long voiceId;
    private int chunkIndex;
    private String text;
    private int attempt; // 시도 횟수
}
