package com.talet.talet.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorEnum {
    /* ===== 공통(Common) ===== */
    COMMON_INVALID_REQUEST(400, "COMMON_INVALID_REQUEST", "잘못된 요청입니다."),
    COMMON_METHOD_NOT_ALLOWED(405, "COMMON_METHOD_NOT_ALLOWED", "허용되지 않은 HTTP 메서드입니다."),
    COMMON_UNSUPPORTED_MEDIA_TYPE(415, "COMMON_UNSUPPORTED_MEDIA_TYPE", "지원하지 않는 미디어 타입입니다."),
    COMMON_INTERNAL_ERROR(500, "COMMON_INTERNAL_ERROR", "서버 내부 오류가 발생했습니다."),

    /* ===== 인증/보안(Auth) ===== */
    AUTH_UNAUTHORIZED(401, "AUTH_UNAUTHORIZED", "인증이 필요합니다."),
    AUTH_FORBIDDEN(403, "AUTH_FORBIDDEN", "접근 권한이 없습니다."),
    AUTH_TOKEN_EXPIRED(401, "AUTH_TOKEN_EXPIRED", "토큰이 만료되었습니다."),
    AUTH_TOKEN_INVALID(401, "AUTH_TOKEN_INVALID", "유효하지 않은 토큰입니다."),
    AUTH_CLAIM_PARSING_FAILED(401, "AUTH_CLAIM_PARSING_FAILED", "사용자 정보를 가져오는 중 오류가 발생했습니다."),

    /* ===== 사용자(User) ===== */
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(409, "USER_ALREADY_EXISTS", "이미 존재하는 사용자입니다."),
    USER_INVALID_INPUT(400, "USER_INVALID_INPUT", "잘못된 사용자 입력입니다."),

    /* ===== 리소스/데이터(Resource) ===== */
    RESOURCE_NOT_FOUND(404, "RESOURCE_NOT_FOUND", "리소스를 찾을 수 없습니다."),
    RESOURCE_CONFLICT(409, "RESOURCE_CONFLICT", "리소스 충돌이 발생했습니다."),

    /* ===== 외부 시스템/서버(Server) ===== */
    SERVER_BAD_GATEWAY(502, "SERVER_BAD_GATEWAY", "외부 서비스 오류(Bad Gateway)입니다."),
    SERVER_TIMEOUT(504, "SERVER_TIMEOUT", "외부 서비스 응답 지연(Timeout)입니다.");

    // 인증실패


    private final int status;
    private final String code;
    private final String message;
}
