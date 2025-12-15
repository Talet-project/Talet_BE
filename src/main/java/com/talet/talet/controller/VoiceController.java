package com.talet.talet.controller;

import com.talet.talet.dto.VoiceRequestDTO;
import com.talet.talet.dto.VoiceResponseDTO;
import com.talet.talet.entity.VoiceFile;
import com.talet.talet.exception.CustomException;
import com.talet.talet.service.VoiceFileService;
import com.talet.talet.util.TaletApiResponse;
import com.talet.talet.util.ErrorEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/voice")
@RequiredArgsConstructor
public class VoiceController {
    private final VoiceFileService voiceFileService;

    // 음성파일 업로드
    @PostMapping
    public ResponseEntity<?> uploadVoice(@RequestHeader("Authorization") String token, @RequestParam("voice") MultipartFile voice, @RequestParam("profile") MultipartFile profile, @ModelAttribute VoiceRequestDTO dto) {
        boolean isUpload = voiceFileService.uploadVoice(token, voice, profile, dto.getFileName());
        if (!isUpload) {
            throw new CustomException(ErrorEnum.COMMON_INTERNAL_ERROR);
        }
        return ResponseEntity.ok(TaletApiResponse.successMessage("음성 파일이 정상적으로 저장되었습니다."));
    }

    // 음성파일 리스트 조회
    @GetMapping
    public ResponseEntity<?> listVoices(@AuthenticationPrincipal UserDetails userDetails) {
        List<VoiceResponseDTO> voices = voiceFileService.getVoiceFiles(userDetails.getUsername());
        return ResponseEntity.ok(TaletApiResponse.success(voices));
    }

    // 음성파일 다운로드
//    @GetMapping("/{voiceFileId}")
//    public ResponseEntity<?> downloadVoice(@PathVariable Long voiceFileId) {
//        VoiceFile voiceFile = voiceFileService.getVoiceFile(voiceFileId);
//        Resource resource = new FileSystemResource(voiceFile.getFilePath());
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=\"" + resource.getFilename() + "\"")
//                .body(TaletApiResponse.success(resource));
//    }

    // 음성파일 삭제
    @DeleteMapping("/{voiceFileId}")
    public ResponseEntity<?> deleteVoice(@PathVariable Long voiceFileId) {
        voiceFileService.deleteVoiceFile(voiceFileId);
        return ResponseEntity.ok(TaletApiResponse.successMessage("음성파일이 정상적으로 삭제되었습니다."));
    }
}
