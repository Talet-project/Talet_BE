package com.talet.talet.controller;

import com.talet.talet.docs.auth.*;
import com.talet.talet.dto.AdminRequestDTO;
import com.talet.talet.dto.SignUpDTO;
import com.talet.talet.dto.TokenRequest;
import com.talet.talet.dto.TokenResponse;
import com.talet.talet.exception.CustomException;
import com.talet.talet.service.AuthService;
import com.talet.talet.util.TaletApiResponse;
import com.talet.talet.util.ErrorEnum;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpClient;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 구글 로그인
    @Operation(summary = "구글 로그인", description = "구글 로그인을 위한 api")
    @AuthLogin
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody TokenRequest request) {
        TokenResponse token = authService.handleGoogleLogin(request.getIdToken());
        if (token.getSignUpToken() != null) {
            log.info("구글 회원가입 토큰 발급");
            return ResponseEntity.status(HttpStatus.CREATED).body(TaletApiResponse.success(token));
        }
        log.info("구글 로그인 토큰 발급");
        return ResponseEntity.ok(TaletApiResponse.success(token));
    }

    // 애플 로그인
    @Operation(summary = "애플 로그인", description = "애플 로그인을 위한 api")
    @AuthLogin
    @PostMapping("/apple")
    public ResponseEntity<?> appleLogin(@RequestBody TokenRequest request) {
        TokenResponse token = authService.handleAppleLogin(request.getIdToken());
        if (token.getSignUpToken() != null) {
            log.info("애플 회원가입 토큰 발급");
            return ResponseEntity.status(HttpStatus.CREATED).body(TaletApiResponse.success(token));
        }
        log.info("애플 로그인 토큰 발급");
        return ResponseEntity.ok(TaletApiResponse.success(token));
    }

    // 구글 회원가입
    @Operation(summary = "구글 회원가입", description = "구글 회원가입을 위한 api", security = {@SecurityRequirement(name = "bearerAuth")})
    @AuthSignUp
    @PostMapping("/google/sign-up")
    public ResponseEntity<?> googleSignUp(@RequestBody SignUpDTO signUpDTO, @RequestHeader("Authorization") String token) {
        TokenResponse tokenResponse = authService.googleSignUp(signUpDTO, token);
        if (tokenResponse == null) {
            log.warn("구글 회원가입 : 사용자가 이미 존재");
            throw new CustomException(ErrorEnum.USER_ALREADY_EXISTS);
        }
        log.info("구글 회원가입 완료");
        return ResponseEntity.ok(TaletApiResponse.success(tokenResponse));
    }

    // 애플 회원가입
    @Operation(summary = "애플 회원가입", description = "애플 회원가입을 위한 api", security = {@SecurityRequirement(name = "bearerAuth")})
    @AuthSignUp
    @PostMapping("/apple/sign-up")
    public ResponseEntity<?> appleSignUp(@RequestBody SignUpDTO signUpDTO, @RequestHeader("Authorization") String token) {
        TokenResponse tokenResponse = authService.appleSignUp(signUpDTO, token);
        if (tokenResponse == null) {
            log.warn("애플 회원가입 : 사용자가 이미 존재");
            throw new CustomException(ErrorEnum.USER_ALREADY_EXISTS);
        }
        log.info("애플 회원가입 완료");
        return ResponseEntity.ok(TaletApiResponse.success(tokenResponse));
    }

    @Operation(summary = "토큰 재발급", description = "토큰 재발급을 위한 api\nAccess 토큰 만료시 Refresh 토큰까지 전부 재발급", security = {@SecurityRequirement(name = "bearerAuth")})
    @AuthRefresh
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {
        TokenResponse newToken = authService.refreshToken(token);
        return ResponseEntity.ok(TaletApiResponse.success(newToken));
    }

    @Operation(summary = "토큰 인증", description = "토큰이 유효한지 확인하는 api", security = {@SecurityRequirement(name = "bearerAuth")})
    @AuthValidate
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        boolean result = authService.validateToken(token);
        if (!result) {
            throw new CustomException(ErrorEnum.AUTH_TOKEN_EXPIRED);
        }
        return ResponseEntity.noContent().build();
    }


    // 로그아웃
    @Operation(summary = "로그아웃", description = "로그아웃을 위한 api", security = {@SecurityRequirement(name = "bearerAuth")})
    @AuthLogout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token, @AuthenticationPrincipal UserDetails userDetails) {
        authService.removeToken(token);
        log.info("identifier={} : 로그아웃 완료", userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // 탈퇴
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 위한 api", security = {@SecurityRequirement(name = "bearerAuth")})
    @AuthDelete
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMember(@RequestHeader("Authorization") String token, @AuthenticationPrincipal UserDetails userDetails) {
        boolean isDelete = authService.removeMember(token);
        if (!isDelete) {
            log.warn("identifier={} : 탈퇴 실패", userDetails.getUsername());
            throw new CustomException(ErrorEnum.COMMON_INTERNAL_ERROR);
        }
        log.info("identifier={} : 탈퇴 완료", userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // 관리자 로그인
    @Hidden
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody AdminRequestDTO dto) {
        String token = authService.adminLogin(dto.getPassword());
        if (token == null) {
            throw new CustomException(ErrorEnum.USER_INVALID_INPUT);
        }
        return ResponseEntity.ok(TaletApiResponse.success(token));
    }

}
