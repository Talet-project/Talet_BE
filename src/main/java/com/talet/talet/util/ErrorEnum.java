package com.talet.talet.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorEnum {

    /*
     * [codeNumber 규칙] 4자리 = (도메인 1) + (상황 1) + (순번 2)
     *
     * 도메인(1자리) 예시:
     * 9 = COMMON
     * 0 = AUTH
     * 1 = USER
     * 2 = RESOURCE
     * 3 = SERVER(외부연동 포함)
     *
     * 상황(2번째 자리) 예시: (글의 분류 방식 참고)
     * 0 = Validation / 요청값 문제 (MethodArgumentNotValid 등)
     * 1 = 도메인 로직/비즈니스 규칙 위반
     * 2 = 인증/인가 실패
     * 3 = 존재하지 않는 리소스 접근 (Not Found)
     * 4 = 외부 API/서버 연동 문제
     * 5 = 서버 내부 오류
     */
    /* ===== 공통(Common: 9xxx) ===== */
    COMMON_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "9001", "잘못된 요청입니다."),                                     // 400
    COMMON_METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "9002", "허용되지 않은 HTTP 메서드입니다."),               // 405
    COMMON_UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "9003", "지원하지 않는 미디어 타입입니다."),        // 415
    COMMON_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9501", "서버 내부 오류가 발생했습니다."),                   // 500

    /* ===== 인증/보안(Auth: 0xxx) ===== */
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "0201", "인증이 필요합니다."),                                        // 401
    AUTH_FORBIDDEN(HttpStatus.FORBIDDEN, "0202", "접근 권한이 없습니다."),                                           //403
    AUTH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "0203", "토큰이 만료되었습니다."),                                   // 401
    AUTH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "0204", "유효하지 않은 토큰입니다."),                                // 401
    AUTH_CLAIM_PARSING_FAILED(HttpStatus.UNAUTHORIZED, "0205", "사용자 정보를 가져오는 중 오류가 발생했습니다."),         // 401

    /* ===== 사용자(User: 1xxx) ===== */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "1301", "사용자를 찾을 수 없습니다."),                                      // 404
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "1101", "이미 존재하는 사용자입니다."),                                 // 409
    USER_INVALID_INPUT(HttpStatus.BAD_REQUEST, "1001", "잘못된 사용자 입력입니다."),                                // 400

    /* ===== 리소스/데이터(Resource: 2xxx) ===== */
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "2301", "리소스를 찾을 수 없습니다."),                                 // 404
    RESOURCE_CONFLICT(HttpStatus.CONFLICT, "2101", "리소스 충돌이 발생했습니다."),                                  // 409

    /* ===== 외부 시스템/서버(Server: 3xxx) ===== */
    SERVER_BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "3401", "외부 서비스 오류(Bad Gateway)입니다."),                    // 502
    SERVER_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "3402", "외부 서비스 응답 지연(Timeout)입니다.");                    // 504


    // 인증실패


    private final HttpStatus status;
    private final String code;
    private final String message;
}
