package com.talet.talet.docs;

public class SwaggerErrorExamples {

    public static final String COMMON_INVALID_REQUEST = """
        {
            "success": false,
            "message": "잘못된 요청입니다.",
            "data": null,
            "error": {
                "code": "COMMON_INVALID_REQUEST",
                "status": 400,
                "message": "잘못된 요청입니다."
            }
        }
    """;

    public static final String COMMON_METHOD_NOT_ALLOWED = """
        {
            "success": false,
            "message": "허용되지 않은 HTTP 메서드입니다.",
            "data": null,
            "error": {
                "code": "COMMON_METHOD_NOT_ALLOWED",
                "status": 405,
                "message": "허용되지 않은 HTTP 메서드입니다."
            }
        }
    """;

    public static final String COMMON_UNSUPPORTED_MEDIA_TYPE = """
        {
            "success": false,
            "message": "지원하지 않는 미디어 타입입니다.",
            "data": null,
            "error": {
                "code": "COMMON_UNSUPPORTED_MEDIA_TYPE",
                "status": 415,
                "message": "지원하지 않는 미디어 타입입니다."
            }
        }
    """;

    public static final String COMMON_INTERNAL_ERROR = """
        {
            "success": false,
            "message": "서버 내부 오류가 발생했습니다.",
            "data": null,
            "error": {
                "code": "COMMON_INTERNAL_ERROR",
                "status": 500,
                "message": "서버 내부 오류가 발생했습니다."
            }
        }
    """;

    public static final String AUTH_UNAUTHORIZED = """
        {
            "success": false,
            "message": "인증이 필요합니다.",
            "data": null,
            "error": {
                "code": "AUTH_UNAUTHORIZED",
                "status": 401,
                "message": "인증이 필요합니다."
            }
        }
    """;

    public static final String AUTH_FORBIDDEN = """
        {
            "success": false,
            "message": "접근 권한이 없습니다.",
            "data": null,
            "error": {
                "code": "AUTH_FORBIDDEN",
                "status": 403,
                "message": "접근 권한이 없습니다."
            }
        }
    """;

    public static final String AUTH_TOKEN_EXPIRED = """
        {
            "success": false,
            "message": "토큰이 만료되었습니다.",
            "data": null,
            "error": {
                "code": "AUTH_TOKEN_EXPIRED",
                "status": 401,
                "message": "토큰이 만료되었습니다."
            }
        }
    """;

    public static final String AUTH_TOKEN_INVALID = """
        {
            "success": false,
            "message": "유효하지 않은 토큰입니다.",
            "data": null,
            "error": {
                "code": "AUTH_TOKEN_INVALID",
                "status": 401,
                "message": "유효하지 않은 토큰입니다."
            }
        }
    """;

    public static final String AUTH_CLAIM_PARSING_FAILED = """
        {
            "success": false,
            "message": "사용자 정보를 가져오는 중 오류가 발생했습니다.",
            "data": null,
            "error": {
                "code": "AUTH_CLAIM_PARSING_FAILED",
                "status": 401,
                "message": "사용자 정보를 가져오는 중 오류가 발생했습니다."
            }
        }
    """;

    public static final String USER_NOT_FOUND = """
        {
            "success": false,
            "message": "사용자를 찾을 수 없습니다.",
            "data": null,
            "error": {
                "code": "USER_NOT_FOUND",
                "status": 404,
                "message": "사용자를 찾을 수 없습니다."
            }
        }
    """;

    public static final String USER_ALREADY_EXISTS = """
        {
            "success": false,
            "message": "이미 존재하는 사용자입니다.",
            "data": null,
            "error": {
                "code": "USER_ALREADY_EXISTS",
                "status": 409,
                "message": "이미 존재하는 사용자입니다."
            }
        }
    """;

    public static final String USER_INVALID_INPUT = """
        {
            "success": false,
            "message": "잘못된 사용자 입력입니다.",
            "data": null,
            "error": {
                "code": "USER_INVALID_INPUT",
                "status": 400,
                "message": "잘못된 사용자 입력입니다."
            }
        }
    """;

    public static final String RESOURCE_NOT_FOUND = """
        {
            "success": false,
            "message": "리소스를 찾을 수 없습니다.",
            "data": null,
            "error": {
                "code": "RESOURCE_NOT_FOUND",
                "status": 404,
                "message": "리소스를 찾을 수 없습니다."
            }
        }
    """;

    public static final String RESOURCE_CONFLICT = """
        {
            "success": false,
            "message": "리소스 충돌이 발생했습니다.",
            "data": null,
            "error": {
                "code": "RESOURCE_CONFLICT",
                "status": 409,
                "message": "리소스 충돌이 발생했습니다."
            }
        }
    """;

    public static final String SERVER_BAD_GATEWAY = """
        {
            "success": false,
            "message": "외부 서비스 오류(Bad Gateway)입니다.",
            "data": null,
            "error": {
                "code": "SERVER_BAD_GATEWAY",
                "status": 502,
                "message": "외부 서비스 오류(Bad Gateway)입니다."
            }
        }
    """;

    public static final String SERVER_TIMEOUT = """
        {
            "success": false,
            "message": "외부 서비스 응답 지연(Timeout)입니다.",
            "data": null,
            "error": {
                "code": "SERVER_TIMEOUT",
                "status": 504,
                "message": "외부 서비스 응답 지연(Timeout)입니다."
            }
        }
    """;
}
