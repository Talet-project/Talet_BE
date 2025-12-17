package com.talet.talet.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.talet.talet.dto.SignUpDTO;
import com.talet.talet.dto.TokenResponse;
import com.talet.talet.entity.BookMark;
import com.talet.talet.entity.Member;
import com.talet.talet.exception.CustomException;
import com.talet.talet.repository.MemberRepository;
import com.talet.talet.util.*;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Lifecycle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final GoogleTokenVerifier googleVerifier;
    private final ApplePublicKeyProvider appleKeyProvider;
    private final JWTTokenUtil jwtTokenUtil;
    private final MemberRepository memberRepository;
    private final RedisUtil redisUtil;

    @Value("${admin.login.password}")
    private String adminLoginPassword;

    // 사용자가 있는지 확인
    public boolean verifyIdentifier(String identifier) {
        log.info("Identifier={}: 사용자가 있는지 조회", identifier);
        return memberRepository.existsByIdentifier(identifier);
    }

    // 구글 로그인
    public TokenResponse handleGoogleLogin(String idToken) {
        GoogleIdToken.Payload payload = googleVerifier.verify(idToken);
        String identifier = payload.getSubject();
        if (!verifyIdentifier(identifier)) {
            log.info("Identifier={} : 구글이메일 없음 - 회원가입", identifier);
            return generateSignUpJwtToken(identifier);
        }
        log.info("Identifier={} : 구글 로그인", identifier);
        return generateJwtToken(identifier);
    }

    // 애플 로그인
    public TokenResponse handleAppleLogin(String idToken) {
        Claims claims = appleKeyProvider.verify(idToken);
        String identifier = claims.getSubject();
        if (!verifyIdentifier(identifier)) {
            log.info("Identifier={} : 애플이메일 없음 - 회원가입", identifier);
            return generateSignUpJwtToken(identifier);
        }
        log.info("Identifier={} : 애플 로그인",  identifier);
        return generateJwtToken(identifier);
    }

    // 구글 회원가입
    public TokenResponse googleSignUp(SignUpDTO dto, String token) {
        String identifier = jwtTokenUtil.getIdentifierFromToken(token);
        if (verifyIdentifier(identifier)) {
            // 사용자가 존재
            log.warn("Identifier={} : 구글 이메일 존재 - 회원가입 불가능", identifier);
            return null;
        }
        dto.setIdentifier(identifier);
        dto.setPlatform("Google");
        Member member = signUp(dto);
        log.info("Identifier={} : 구글 회원가입 완료", member.getIdentifier());
        return generateJwtToken(member.getIdentifier());
    }

    // 애플 회원가입
    public TokenResponse appleSignUp(SignUpDTO dto, String token) {
        String identifier = jwtTokenUtil.getIdentifierFromToken(token);
        if (verifyIdentifier(identifier)) {
            // 사용자가 존재
            log.warn("Identifier={} : 애플 이메일 존재 - 회원가입 불가능", identifier);
            return null;
        }
        dto.setIdentifier(identifier);
        dto.setPlatform("Apple");
        Member member = signUp(dto);
        log.info("Identifier={} : 애플 회원가입 완료", member.getIdentifier());
        return generateJwtToken(member.getIdentifier());
    }

    // 토큰 생성(email로)
    private TokenResponse generateJwtToken(String identifier) {
        String accessToken = jwtTokenUtil.createAccessToken(identifier);
        String refreshToken = jwtTokenUtil.createRefreshToken(identifier);
        redisUtil.saveToken(RedisTokenType.ACCESS_TOKEN, identifier, accessToken);
        redisUtil.saveToken(RedisTokenType.REFRESH_TOKEN, identifier, refreshToken);
        log.info("Identifier={} : 로그인 토큰 생성", identifier);
        return new TokenResponse(accessToken, refreshToken);
    }

    // 회원가입 전용 토큰(subject로)
    private TokenResponse generateSignUpJwtToken(String identifier) {
        String signUpToken = jwtTokenUtil.createSignUpToken(identifier);
        redisUtil.saveToken(RedisTokenType.SIGN_UP_TOKEN, identifier, signUpToken);
        log.info("Identifier={} : 회원가입 토큰 생성", identifier);
        return new TokenResponse(signUpToken);
    }

    public TokenResponse refreshToken(String token) {
        String identifier = jwtTokenUtil.getIdentifierFromToken(token);
        return generateJwtToken(identifier);
    }

    public boolean validateToken(String token) {
        token = jwtTokenUtil.getPureToken(token);
        String identifier = jwtTokenUtil.getIdentifierFromToken(token);
        String tokenType = jwtTokenUtil.getTokenType(token);
        RedisTokenType type = RedisTokenType.valueOf(tokenType);
        boolean redisResult = redisUtil.validateToken(type, identifier, token);
        boolean jwtResult = jwtTokenUtil.validateToken(token);
        return redisResult && jwtResult;
    }

    // 회원가입
    public Member signUp(SignUpDTO dto) {
        String role = "USER";
        Member member = new Member();
        member.setIdentifier(dto.getIdentifier());
        member.setName(dto.getName());
        member.setBirthDate(dto.getBirthDate());
        member.setGender(dto.getGender());
        member.setNativeLanguages(dto.getNativeLanguages());
        member.setPlatform(dto.getPlatform());
        member.setRole(role);
        log.info("Identifier={} : 회원가입 성공",  dto.getIdentifier());
        return memberRepository.save(member);
    }

    // 사용자 토큰 삭제
    public void removeToken(String token) {
        String identifier = jwtTokenUtil.getIdentifierFromToken(token);
        redisUtil.deleteToken(RedisTokenType.ACCESS_TOKEN, identifier);
        redisUtil.deleteToken(RedisTokenType.REFRESH_TOKEN, identifier);
        log.info("Identifier={} : 사용자 토큰 삭제", identifier);
    }

    public boolean removeMember(String token) {
        String identifier = jwtTokenUtil.getIdentifierFromToken(token);
        Member member = null;
        if (memberRepository.existsByIdentifier(identifier)) {
            member = memberRepository.findByIdentifier(identifier);
        }
        if (member != null) {
            removeToken(identifier);
            memberRepository.delete(member);
            log.info("Identifier={} : 사용자 탈퇴", identifier);
            return  true;
        }
        log.warn("사용자 탈퇴 실패:이유모름");
        return false;
    }

    public String adminLogin(String pwd) {
        if (!adminLoginPassword.equals(pwd)) {
            return null;
        }
        String adminToken = jwtTokenUtil.createAdminToken();
        redisUtil.saveToken(RedisTokenType.ADMIN_TOKEN, "admin", adminToken);
        return adminToken;
    }
}
