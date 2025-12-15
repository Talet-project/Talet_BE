package com.talet.talet.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talet.talet.dto.TTSJob;
import com.talet.talet.entity.FairyTaleBook;
import com.talet.talet.entity.Member;
import com.talet.talet.entity.VoiceFile;
import com.talet.talet.repository.MemberRepository;
import com.talet.talet.repository.VoiceFileRepository;
import com.talet.talet.service.FairyTaleBookService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TTSWorker {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final WebClient ttsWebClient;
    private final MemberRepository memberRepository;
    private final FairyTaleBookService  fairyTaleBookService;
    private final VoiceFileRepository voiceFileRepository;

    private static final String TTS_QUEUE_KEY = "tts:queue";
    private static final String TTS_RESULT_KEY_PREFIX = "tts:result:";
    private static final String TTS_JOBDATA_KEY_PREFIX = "tts:jobdata:";
    private static final int MAX_ATTEMPT = 3;

    @Value("${storage.base-dir}")
    private String baseDir;

    @Value("${storage.tts-root-dir}")
    private String ttsUploadDir;

    @Value("${storage.tts-url-prefix}")
    private String ttsUrl;

    @Value("${tts.worker.concurrency:3}")
    private int workerCount;

    @PostConstruct
    public void start() {
        restoreProcessingJobs();
        for (int i = 0; i < workerCount; i++) {
            Thread workerThread = new Thread(this::consumeLoop, "tts-worker-thread-" + i);
            workerThread.setDaemon(true);
            workerThread.start();
        }
    }

    private void restoreProcessingJobs() {
        Set<String> keys = redisTemplate.keys(TTS_RESULT_KEY_PREFIX + "*:status");
        if (keys == null) {
            return;
        }

        for (String statusKey : keys) {
            String status = redisTemplate.opsForValue().get(statusKey);
            if (!"processing".equals(status)) continue;

            String jobId = statusKey.split(":")[2];
            String jobJson = redisTemplate.opsForValue().get(TTS_JOBDATA_KEY_PREFIX + jobId);

            if (jobJson != null) {
                log.warn("🔄 서버 재시작 복구 → jobId={}", jobId);
                redisTemplate.opsForList().leftPush(TTS_QUEUE_KEY, jobJson);
            }
        }
    }

    private void consumeLoop() {
        while (true) {
            try {
                String json = blockingRightPop(TTS_QUEUE_KEY); // 0 = 무기한 대기
                if (json == null) {
                    // 타임아웃으로 null이 올 수도 있음 (timeoutSeconds > 0 인 경우)
                    continue;
                }

                TTSJob job = objectMapper.readValue(json, TTSJob.class);

                log.info("TTS Job 수신: jobId={}, bookId={}, memberId={}, chunkIndex={}, attempt={}",
                        job.getJobId(), job.getBookId(), job.getMemberId(), job.getChunkIndex(), job.getAttempt());

                String baseKey = TTS_RESULT_KEY_PREFIX + job.getJobId();
                // 상태: processing
                redisTemplate.opsForValue().set(baseKey + ":status", "processing");
                redisTemplate.opsForValue().set(TTS_JOBDATA_KEY_PREFIX + job.getJobId(), json);

                try {
                    processJob(job);
                } catch (Exception e) {
                    log.error("TTS Job 처리 중 에러 - jobId={}, attempt={}",
                            job.getJobId(), job.getAttempt(), e);
                    handleFailure(job, e);
                }

            } catch (Exception e) {
                log.error("TTS Worker 루프 에러 발생", e);
                // 너무 빠르게 뺑뺑이 도는 것을 막기 위해 잠깐 sleep
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private String blockingRightPop(String key) {
        while (!Thread.currentThread().isInterrupted()) {
            String value = redisTemplate.opsForList().rightPop(key);

            if (value != null) {
                return value; // 작업 있으면 처리
            }

            // 2) 잠깐 쉬었다가 다시 시도 (1초)
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return null;
    }

    private void handleFailure(TTSJob job, Exception e) {
        String baseKey = TTS_RESULT_KEY_PREFIX + job.getJobId();

        int nextAttempt = job.getAttempt() + 1;
        job.setAttempt(nextAttempt);

        // 에러 메시지 저장 (iOS에서 조회할 수 있게)
        String errorMsg = Optional.ofNullable(e.getMessage()).orElse("unknown error");
        redisTemplate.opsForValue().set(baseKey + ":lastError", errorMsg);

        if (nextAttempt <= MAX_ATTEMPT) {
            // 재시도 가능 → 다시 큐에 넣음
            try {
                String retryJson = objectMapper.writeValueAsString(job);

                // 상태: retrying
                redisTemplate.opsForValue().set(baseKey + ":status", "retrying");

                // leftPush 로 재시도 작업을 앞으로 보내도 되고, rightPush 로 FIFO 유지해도 됨
                redisTemplate.opsForList().leftPush(TTS_QUEUE_KEY, retryJson);

                redisTemplate.opsForValue().set(TTS_JOBDATA_KEY_PREFIX + job.getJobId(), retryJson);

                log.warn("TTS Job 재시도 예약 - jobId={}, nextAttempt={}", job.getJobId(), nextAttempt);
            } catch (JsonProcessingException ex) {
                log.error("TTS Job 재직렬화 실패 - jobId={}", job.getJobId(), ex);
                // 직렬화 실패 시에는 어쩔 수 없이 failed 처리
                redisTemplate.opsForValue().set(baseKey + ":status", "failed");
            }
        } else {
            // 재시도 초과 → 최종 실패
            redisTemplate.opsForValue().set(baseKey + ":status", "failed");
            log.error("TTS Job 최종 실패 - jobId={}, attempt={}", job.getJobId(), job.getAttempt());
        }
    }

    private void processJob(TTSJob job) throws Exception {
        Member member = memberRepository.findByMemberId(job.getMemberId());
        FairyTaleBook book = fairyTaleBookService.getBook(job.getBookId());
        VoiceFile voice = voiceFileRepository.findById(job.getVoiceId());

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("text", job.getText());
        String userProfileJson = objectMapper.writeValueAsString(
                Map.of("nickname", member.getName(), "languages", job.getLanguage())
        );
        builder.part("user_profile",  userProfileJson);
        builder.part("fairy_tale_name", book.getName());

        Path speakerAudioPath = Path.of(baseDir + voice.getFilePath());
        builder.part("speaker_audio", new FileSystemResource(speakerAudioPath));

        MultiValueMap<String, HttpEntity<?>> multipartData = builder.build();

        // 1) Python TTS 서버 호출 (예: /tts API)
        // 이 부분 고치면 됨 - 파라미터 추가로 넣어야함
        byte[] audioBytes = ttsWebClient.post()
                .uri("/synthesize/custom")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipartData))
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        resp -> resp.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException(body)))
                )
                .bodyToMono(byte[].class)
                .block();

        if (audioBytes == null || audioBytes.length == 0) {
            throw new RuntimeException("TTS 서버로부터 유효한 오디오 데이터를 받지 못했습니다. jobId=" + job.getJobId());
        }

        // 2) 음성 파일 저장 경로 (예시)
        Path dir = Path.of(ttsUploadDir, String.valueOf(job.getMemberId()), String.valueOf(job.getBookId()));
        Files.createDirectories(dir);

        Path output = dir.resolve(String.format("%03d_%s.wav", job.getChunkIndex(), job.getJobId()));
        Files.write(output, audioBytes);

        String relativePath = String.format("%s/%s/%03d_%s.wav", job.getMemberId(), job.getBookId(), job.getChunkIndex(), job.getJobId());

        // 3) Redis에 결과 & 상태 저장
        String baseKey = TTS_RESULT_KEY_PREFIX + job.getJobId();
        redisTemplate.opsForValue().set(baseKey + ":status", "done");
        redisTemplate.opsForValue().set(baseKey + ":path", ttsUrl + relativePath);

        log.info("TTS Job 완료: jobId={}, path={}", job.getJobId(), ttsUrl + relativePath);
    }

}
