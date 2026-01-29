package com.talet.talet.controller;

import com.talet.talet.dto.TTSDefaultDTO;
import com.talet.talet.dto.TTSRequestDTO;
import com.talet.talet.dto.TTSResultRequestDTO;
import com.talet.talet.service.TTSService;
import com.talet.talet.util.ErrorEnum;
import com.talet.talet.util.TaletApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/tts")
@RequiredArgsConstructor
public class TTSController {
    private final TTSService ttsService;
    private final StringRedisTemplate redisTemplate;

    private static final String TTS_RESULT_KEY_PREFIX = "tts:result:";

    @Operation(summary = "동화책 내용 요청", description = "TTS를 사용한 동화책 내용 요청하기")
    @PostMapping("/book")
    public ResponseEntity<TaletApiResponse<?>> requestTTSForBook(@RequestBody TTSRequestDTO dto, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<String> jobIds = ttsService.requestTTSForBook(dto.getBookId(), userDetails.getUsername(), dto.getVoiceId());
            return ResponseEntity.ok(TaletApiResponse.success(jobIds, "TTS 요청이 정상적으로 접수되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(TaletApiResponse.error(ErrorEnum.COMMON_INTERNAL_ERROR));
        }
    }

    @Operation(summary = "기본음성 동화책 api", description = "기본 음성 api로 동화책 읽어주기")
    @GetMapping("/default/book")
    public Mono<ResponseEntity<?>> requestTTSApi(@RequestBody TTSDefaultDTO dto) {
        return ttsService.bookTts(dto).map(ResponseEntity::ok);
    }

    @Operation(summary = "동화책 내용 받아오기", description = "polling 으로 동화책 내용 받아오기")
    @GetMapping("/result")
    public ResponseEntity<TaletApiResponse<?>> getTTSResult(@RequestBody TTSResultRequestDTO ttsResultRequestDTO) {
        String baseKey = TTS_RESULT_KEY_PREFIX + ttsResultRequestDTO.getJobId();
        String status = redisTemplate.opsForValue().get(baseKey + ":status");

        HashMap<String, String> map = new HashMap<>();
        map.put("status", "processing");

        if (status == null || !"done".equals(status)) {
            return ResponseEntity.ok(TaletApiResponse.success(map, "아직 처리중입니다."));
        }
        String path = redisTemplate.opsForValue().get(baseKey + ":path");
        map.put("status", "done");
        map.put("path", path);
        return ResponseEntity.ok(TaletApiResponse.success(map, "TTS 결과가 준비되었습니다."));
    }
}
