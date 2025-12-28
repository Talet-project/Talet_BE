package com.talet.talet.service;

import com.talet.talet.dto.VoiceRequestDTO;
import com.talet.talet.dto.VoiceResponseDTO;
import com.talet.talet.entity.Member;
import com.talet.talet.entity.VoiceFile;
import com.talet.talet.repository.MemberRepository;
import com.talet.talet.repository.VoiceFileRepository;
import com.talet.talet.util.JWTTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoiceFileService {
    @Value("${storage.voices-root-dir}")
    private String voiceUploadDir; // ~~~/Talet/voices

    @Value("${storage.voice-profiles-dir}")
    private String voiceProfilesDir;

    @Value("${storage.public-base-url}")
    private String publicBaseUrl;

    private final MemberRepository memberRepository;
    private final JWTTokenUtil jwtTokenUtil;
    private final VoiceFileRepository voiceFileRepository;

    @Transactional
    public boolean uploadVoice(String token, MultipartFile voice, MultipartFile profile, VoiceRequestDTO dto) {
        String identifier = jwtTokenUtil.getIdentifierFromToken(token);
        Member member = memberRepository.findByIdentifier(identifier);
        String folderPath = voiceUploadDir + "/" + identifier;
        String profileDir = voiceProfilesDir + "/" + identifier;
        if (!mkdir(folderPath) || !mkdir(profileDir)) {
            return false; // 폴더 생성 실패
        }
        String voicePath = uploadVoiceFile(voice, identifier);
        if (voicePath == null) {
            return false;
        }
        String profilePath= addProfile(profile, identifier);
        if (profilePath == null) {
            return false;
        }
        VoiceFile voiceFile = new VoiceFile();
        voiceFile.setFileName(dto.getFileName());
        voiceFile.setFilePath(voicePath);
        voiceFile.setProfile(profilePath);
        voiceFile.setLanguage(dto.getLanguage());
        voiceFile.setMember(member);
        voiceFileRepository.save(voiceFile);
        return true;
    }

    // 폴더 생성
    public boolean mkdir(String path) {
        File folder = new  File(path);
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                // 폴더 생성 성공
            } else {
                return false; // 폴더 생성 실패
            }
        }
        return true;
    }

    // 보이스 파일 추가
    public String uploadVoiceFile(MultipartFile voice, String identifier) {
        String uuidName = UUID.randomUUID().toString() + "_" + voice.getOriginalFilename();
//        File dest = new File(voiceUploadDir, identifier, uuidName);
        Path path = Paths.get(voiceUploadDir, identifier, uuidName);
        try {
            Files.createDirectories(path.getParent());
            voice.transferTo(path.toFile());
        } catch (IOException e) {
            return null;
        }
        return "/voices/" + identifier + "/" + uuidName;
    }

    // 프로필 이미지 추가
    public String addProfile(MultipartFile profile, String identifier) {
        String original = profile.getOriginalFilename();
        if (original == null) return null;

        String ext = original.substring(original.lastIndexOf("."));
        String uuidName = UUID.randomUUID() + ext;

        Path realPath = Paths.get(voiceProfilesDir, identifier, uuidName); // /.../images/voices/{id}/{uuid}
        try {
            Files.createDirectories(realPath.getParent());
            Files.copy(profile.getInputStream(), realPath);
        } catch (IOException e) {
            return null;
        }
        // DB에는 URL 기준 상대 경로
        return "/images/voices/" + identifier + "/" + uuidName;
    }

    // 회원의 모든 음성파일 조회
    @Transactional(readOnly = true)
    public List<VoiceResponseDTO> getVoiceFiles(String identifier) {
        Member member = memberRepository.findByIdentifier(identifier);
        List<VoiceFile> voiceFiles = voiceFileRepository.findByMember_MemberId(member.getMemberId());
        List<VoiceResponseDTO> voiceResponseDTOS = new ArrayList<>();
        for(VoiceFile voiceFile : voiceFiles) {
            VoiceResponseDTO voiceResponseDTO = new VoiceResponseDTO();
            voiceResponseDTO.setId(voiceFile.getId());
            voiceResponseDTO.setFileName(voiceFile.getFileName());
            voiceResponseDTO.setFilePath(publicBaseUrl + voiceFile.getFilePath());
            voiceResponseDTO.setProfile(publicBaseUrl + voiceFile.getProfile());
            voiceResponseDTO.setLanguage(voiceFile.getLanguage());
            voiceResponseDTOS.add(voiceResponseDTO);
        }
        return voiceResponseDTOS;
    }

//    // 음성파일 다운로드 (엔티티 반환)
//    @Transactional(readOnly = true)
//    public VoiceFile getVoiceFile(Long fileId) {
//        return voiceFileRepository.findById(fileId)
//                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
//    }

    // 음성파일 삭제
    @Transactional
    public void deleteVoiceFile(Long fileId) {
        VoiceFile file = voiceFileRepository.findById(fileId);
        // 실제 파일 삭제
        File diskFile = new File(file.getFilePath());
        if (diskFile.exists()) {
            diskFile.delete();
        }
        voiceFileRepository.delete(file);
    }


}
