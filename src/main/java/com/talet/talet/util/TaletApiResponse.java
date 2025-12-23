package com.talet.talet.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "표준 API 응답 래퍼")
public class TaletApiResponse<T> {
    @Schema(description = "성공 여부", example = "true")
    private boolean success;
    @Schema(description = "메시지", example = "요청이 성공적으로 처리되었습니다.")
    private String message;
    @Schema(description = "응답 데이터")
    private T data;
    @Schema(description = "오류 정보(실패 시 포함)", nullable = true)
    private ErrorResponse error;

    // 데이터 + 메세지
    public static <T> TaletApiResponse<T> success(T data, String message) {
        return TaletApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .error(null)
                .build();
    }

    // 데이터
    public static <T> TaletApiResponse<T> success(T data) {
        return success(data, "요청이 성공적으로 처리되었습니다.");
    }

    // 메세지
    public static <T> TaletApiResponse<T> successMessage(String message) {
        return TaletApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(null)
                .error(null)
                .build();
    }

    public static <T> TaletApiResponse<T> error(ErrorResponse error) {
        return TaletApiResponse.<T>builder()
                .success(false)
                .message(error.getMessage())
                .data(null)
                .error(error)
                .build();
    }

    public static <T> TaletApiResponse<T> error(String code, HttpStatus status, String message, List<String> details) {
        return error(ErrorResponse.of(code, status, message, details));
    }

    public static <T> TaletApiResponse<T> error(String code, HttpStatus status, String message) {
        return error(ErrorResponse.of(code, status, message, null));
    }
    public static <T> TaletApiResponse<T> error(ErrorEnum errorEnum, List<String> details) {
        return error(ErrorResponse.of(errorEnum.getCode(), errorEnum.getStatus(), errorEnum.getMessage(), details));
    }

    public static <T> TaletApiResponse<T> error(ErrorEnum errorEnum) {
        return error(ErrorResponse.of(errorEnum.getCode(), errorEnum.getStatus(), errorEnum.getMessage(), null));
    }


    @Getter
    @AllArgsConstructor
    @Schema(description = "오류 응답 본문")
    public static class ErrorResponse {
        @Schema(description = "에러 코드", example = "AUTH_001")
        private String code;
        @Schema(description = "HTTP 상태", example = "401")
        private HttpStatus status;
        @Schema(description = "에러 메시지", example = "인증 토큰이 유효하지 않습니다.")
        private String message;
        @Schema(description = "상세 메시지 목록", example = "[\"idToken is invalid\"]")
        private List<String> details;

        public static ErrorResponse of(String code, HttpStatus status, String message) {
            return new ErrorResponse(code, status, message, null);
        }
        public static ErrorResponse of(String code, HttpStatus status, String message, List<String> details) {
            return new ErrorResponse(code, status, message, details);
        }
    }
}
