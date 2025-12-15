package com.talet.talet.docs;

public class SwaggerExamples {

    public static final String LOGIN_SUCCESS = """
        {
            "success": true,
            "message": "요청이 성공적으로 처리되었습니다.",
            "data": {
                "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                "signUpToken": null
            },
            "error": null
        }
    """;

    public static final String SIGNUP_TOKEN_ISSUED = """
        {
            "success": true,
            "message": "요청이 성공적으로 처리되었습니다.",
            "data": {
                "accessToken": null,
                "refreshToken": null,
                "signUpToken": "SIGNUP-1234"
            },
            "error": null
        }
    """;

    public static final String TOKEN_RESPONSE_OK = """
        {
            "success": true,
            "message": "요청이 성공적으로 처리되었습니다.",
            "data": {
                "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                "signUpToken": null
            },
            "error": null
        }
    """;

    public static final String LOGOUT_OK = """
        {
            "success": true,
            "message": "로그아웃이 성공적으로 완료되었습니다.",
            "error": null
        }
    """;

    public static final String DELETE_OK = """
        {
            "success": true,
            "message": "성공적으로 탈퇴가 완료되었습니다.",
            "error": null
        }
    """;

    //============================================================
    public static final String BOOK_SEARCH_OK = """
        {
            "success": true,
            "message": "요청이 성공적으로 처리되었습니다.",
            "data": [
                {
                    "id": "UUID-0001-0001-0001",
                    "name": "토끼와 거북이",
                    "thumbnail": "https://talet.site/image/book_thumbnail.jpg",
                    "tag": ["지혜", "선과 악", "나눔"],
                    "plot": "느림보 거북이가 결국 토끼를 이기는 이야기입니다."
                },
                {
                    "id": "UUID-0002-0002-0002",
                    "name": "흥부와 놀부",
                    "thumbnail": "https://talet.site/image/book_thumbnail.jpg",
                    "tag": ["지혜", "선과 악", "나눔"],
                    "plot": "흥부가 제비를 도와주고 보물을 받는 이야기입니다."
                },
                {
                    "id": "UUID-0003-0003-0003",
                    "name": "콩쥐 팥쥐",
                    "thumbnail": "https://talet.site/image/book_thumbnail.jpg",
                    "tag": ["지혜", "선과 악", "나눔"],
                    "plot": "뭐... 아무튼 콩쥐가 승리자인 이야기입니다."
                }
            ],
            "error": null
        }
    """;

    public static final String BOOK_DETAIL_OK = """
        {
            "success": true,
            "message": "오청이 성공적으로 처리되었습니다.",
            "data": {
                "id": "UUID-0001-0001-0001",
                "name": "토끼와 거북이",
                "thumbnail": "https://talet.site/image/book_thumbnail.jpg",
                "stillImages": [
                    "https://talet.site/images/stillcut1.jpg",
                    "https://talet.site/images/stillcut2.jpg"
                ],
                "tags": ["지혜", "선과 악", "나눔"],
                "shorts": {
                    "ko": "토끼와 거북이의 경주 이야기",
                    "en": "A race between a rabbit and a turtle."
                },
                "plots": {
                    "ko": "느림보 거북이가 결국 토끼를 이기는 이야기입니다.",
                    "en": "A slow turtle eventually wins against the overconfident rabbit."
                },
                "bookmark": true
            },
            "error": null
        }
    """;

    public static final String BOOK_LOOKING_OK = """
        {
            "success": true,
            "message": "오청이 성공적으로 처리되었습니다.",
            "data": {
                "id": "UUID-0001-0001-0001",
                "name": "토끼와 거북이",
                "thumbnail": "https://talet.site/image/book_thumbnail.jpg",
                "tags": ["지혜", "선과 악", "나눔"],
                "shorts": {
                    "ko": "토끼와 거북이의 경주 이야기",
                    "en": "A race between a rabbit and a turtle."
                },
                "bookmark": true
            },
            "error": null
        }
    """;

    //============================================================
    public static final String MEMBER_FIND_OK = """
        {
            "success": true,
            "message": "요청이 성공적으로 처리되었습니다.",
            "data": {
                "profileImage": "https://talet.site/images/profile1.jpg",
                "nickname": "홍길동",
                "gender": "남성",
                "birthday": "2019-01",
                "languages": ["KOREAN", "ENGLISH"]
            },
            "error": null
        }
    """;
}
