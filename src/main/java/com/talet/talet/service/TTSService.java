package com.talet.talet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talet.talet.dto.TTSJob;
import com.talet.talet.entity.FairyTaleBook;
import com.talet.talet.entity.Member;
import com.talet.talet.entity.VoiceFile;
import com.talet.talet.repository.MemberRepository;
import com.talet.talet.repository.VoiceFileRepository;
import com.talet.talet.util.LanguageEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TTSService {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final FairyTaleBookService fairyTaleBookService;
    private final MemberRepository memberRepository;

    private static final String TTS_QUEUE_KEY = "tts:queue";
    private static final int CHUNK_SIZE = 8;
    private final RedisTemplate<String, String> redisTemplate;

    public List<String> requestTTSForBook(String bookId, String identifier, Long voiceId) {
        Member member = memberRepository.findByIdentifier(identifier);
        String memberId = member.getMemberId();
        FairyTaleBook book = fairyTaleBookService.getBook(bookId);
        String fullText = fairyTaleBookService.getContent(book.getEnglishName());
        System.out.println(2);
        String language = LanguageEnum.KOREAN.toString();
        if (member.getNativeLanguages().size() > 1) {
            for (LanguageEnum lan: member.getNativeLanguages()) {
                if (lan != LanguageEnum.KOREAN) {
                    language = lan.toString();
                    break;
                }
            }
        }
        List<String> lines = fullText.lines().toList();
        List<String> jobIds = new ArrayList<>();

        for (int i = 0; i < lines.size(); i += CHUNK_SIZE) {
            List<String> chunk = lines.subList(i, Math.min(i + CHUNK_SIZE, lines.size()));
            String jobId = UUID.randomUUID().toString();
            jobIds.add(jobId);

            TTSJob job = new TTSJob(
                    jobId,
                    bookId,
                    memberId,
                    language,
                    voiceId,
                    i / CHUNK_SIZE,
                    String.join("\n", chunk),
                    0
            );

            try {
                String json = objectMapper.writeValueAsString(job);
                redisTemplate.opsForList().leftPush(TTS_QUEUE_KEY, json);
            } catch (Exception e) {
                throw new RuntimeException("TTS Job 직렬화 실패", e);
            }
        }
        return jobIds;
    }

}
