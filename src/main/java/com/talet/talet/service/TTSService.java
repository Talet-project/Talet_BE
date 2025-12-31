package com.talet.talet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talet.talet.dto.SupertoneTTSRequest;
import com.talet.talet.dto.TTSDefaultDTO;
import com.talet.talet.dto.TTSJob;
import com.talet.talet.entity.FairyTaleBook;
import com.talet.talet.entity.Member;
import com.talet.talet.entity.VoiceFile;
import com.talet.talet.repository.MemberRepository;
import com.talet.talet.repository.VoiceFileRepository;
import com.talet.talet.util.LanguageEnum;
import com.talet.talet.util.TaletApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TTSService {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final FairyTaleBookService fairyTaleBookService;
    private final MemberRepository memberRepository;

    private static final String TTS_QUEUE_KEY = "tts:queue";
    private static final int CHUNK_SIZE = 8;
    private final RedisTemplate<String, String> redisTemplate;
    private final WebClient ttsApiWebClient;

    @Value("${storage.tts-root-dir}")
    private String ttsUploadDir;

    @Value("${storage.tts-url-prefix}")
    private String ttsUrlPrefix;

    public List<String> requestTTSForBook(String bookId, String identifier, Long voiceId) {
        Member member = memberRepository.findByIdentifier(identifier);
        String memberId = member.getMemberId();
        FairyTaleBook book = fairyTaleBookService.getBook(bookId);
        String fullText = fairyTaleBookService.getContent(book.getEnglishName());
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

    public Mono<TaletApiResponse<?>> bookTts(TTSDefaultDTO dto) {
        return test(dto).map(TaletApiResponse::success);
    }

    public Mono<List<String>> test (TTSDefaultDTO dto) {
        FairyTaleBook book = fairyTaleBookService.getBook(dto.getBookId());
        String fullText = fairyTaleBookService.getContent(book.getEnglishName());
        List<String> textLines = Arrays.stream(fullText.split("//////////")).map(String::trim).filter(s -> !s.isBlank()).toList(); // 우선 여기 처리 방식 미뤄두고
        for(String line : textLines) {
            System.out.println(line);
        }
        String voiceId;
        boolean isMan;
        if (dto.getGender().equalsIgnoreCase("man")) {
            voiceId = "068aa76205e4eb612a2fb0";
            isMan = true;
        } else {
            voiceId = "7c8586b2869391ac4c7389";
            isMan = false;
        }


        int concurrency = textLines.size();
        return Flux.fromIterable(textLines)
                .index()
                .flatMap(tuple ->
                        synthesizeToFile(book.getEnglishName(), tuple.getT1().intValue() + 1, voiceId, tuple.getT2(), isMan)
                                .map(this::toPublicUrl)
                                .doOnError(e -> log.error("TTS 실패 index={}, text='{}'", tuple.getT1(), tuple.getT2(), e))
                        , concurrency)
                .collectList()
                .doOnError(e -> log.error("TTS 전체 실패(collectList)", e))
                .doFinally(sig -> log.warn("TTS 종료 signal={}", sig));
    }

    private String toPublicUrl(Path path) {
        return ttsUrlPrefix + path.getFileName();
    }

    private Mono<Path> synthesizeToFile(String book, int count, String voiceId, String text, boolean man) {
        SupertoneTTSRequest request = SupertoneTTSRequest.defaultRequest(text);
        String fileName = book + "_" + count + ".wav";
        Path path;
        if (man) {
            path = Path.of(ttsUploadDir, book, "man").resolve(fileName);
        } else {
            path = Path.of(ttsUploadDir, book, "girl").resolve(fileName);
        }

        return requestWavBytes(voiceId, request, path);
    }

    private Mono<Path> requestWavBytes(String voiceId, SupertoneTTSRequest request, Path path) {
        return ttsApiWebClient.post()
                .uri("/v1/text-to-speech/{voiceId}", voiceId)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(body -> new RuntimeException(
                                        "Supertone API error: " + resp.statusCode().value()
                                                + " body=" + body
                                ))
                )
                .bodyToFlux(DataBuffer.class)
                .as(dataBuffers -> {
                    return Mono.fromCallable(() -> {
                                Files.createDirectories(path.getParent());
                                return path;
                            })
                            .subscribeOn(Schedulers.boundedElastic())
                            .then(DataBufferUtils.write(dataBuffers, path,
                                    StandardOpenOption.CREATE,
                                    StandardOpenOption.TRUNCATE_EXISTING))
                            .thenReturn(path);
                });
    }

    private Mono<Void> writeFile(Path path, byte[] bytes) {
        return Mono.fromRunnable(() -> {
                    try {
                        Files.createDirectories(path.getParent());
                        Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    } catch (Exception e) {
                        throw new RuntimeException("파일 저장 실패: ", e);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

}
