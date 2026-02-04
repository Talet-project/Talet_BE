package com.talet.talet.controller;

import com.talet.talet.docs.member.*;
import com.talet.talet.dto.*;
import com.talet.talet.exception.CustomException;
import com.talet.talet.service.MemberService;
import com.talet.talet.util.TaletApiResponse;
import com.talet.talet.util.ErrorEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 멤버 데이터 보여주기
    @Operation(summary = "유저 데이터", description = "유저 데이터 검색 api", security = {@SecurityRequirement(name = "bearerAuth")})
    @Member
    @GetMapping("/me")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        MemberResponseDTO member = memberService.getUserInfo(userDetails.getUsername());
        log.info("Identifier={} : 사용자 데이터 조회 성공", userDetails.getUsername());
        return ResponseEntity.ok(TaletApiResponse.success(member));
    }

    // 프로필 변경
    @Operation(summary = "유저 정보 변경", description = "유저 정보 변경 api", security = {@SecurityRequirement(name = "bearerAuth")})
    @MemberProfileUpdate
    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserDetails userDetails, @RequestBody MemberRequestDTO memberRequestDTO) {
        MemberResponseDTO member = memberService.memberUpdate(userDetails.getUsername(), memberRequestDTO);
        if (member == null) {
            log.warn("Identifier={} : 사용자 정보 수정 실패-이유모름",  userDetails.getUsername());
            throw new CustomException(ErrorEnum.COMMON_INTERNAL_ERROR);
        }
        log.info("Identifier={} : 사용자 정보 수정 성공", userDetails.getUsername());
        return ResponseEntity.ok(TaletApiResponse.success(member));
    }

    @Operation(summary = "유저 이미지 변경", description = "유저 프로필 사진 변경 api", security = {@SecurityRequirement(name = "bearerAuth")})
    @MemberProfileUpdate
    @PostMapping(value = "/update/image",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateImage(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute MemberProfileImageRequestDTO profileDTO) {
        MemberResponseDTO member = memberService.memberProfileUpdate(userDetails.getUsername(), profileDTO.getProfile());
        if (member == null) {
            log.warn("Identifier={} : 사용자 프로필 사진 수정 실패-이유모름",  userDetails.getUsername());
            throw new CustomException(ErrorEnum.COMMON_INTERNAL_ERROR);
        }
        log.info("Identifier={} : 사용자 프로필 사진 수정 성공", userDetails.getUsername());
        return ResponseEntity.ok(TaletApiResponse.success(member));
    }

    @Operation(summary = "북마크", description = "북마크가 안된 상태라면 추가, 추가된 상태라면 삭제", security = {@SecurityRequirement(name = "bearerAuth")})
    @Bookmark
    @GetMapping("/bookmark")
    public ResponseEntity<?> checkBookmark(@AuthenticationPrincipal UserDetails userDetails, @RequestBody BookmarkRequestDTO bookmarkRequestDTO) {
        // 북마크가 되어있는 상태라면 삭제, 아니면 추가
        memberService.bookmark(bookmarkRequestDTO, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // 지금 읽고있어요 & 찜 목록 - 유저 책장
    @Operation(summary = "내 책장", description = "지금 읽고있어요, 찜 목록 데이터", security = {@SecurityRequirement(name = "bearerAuth")})
    @Bookshelf
    @GetMapping("/bookshelf")
    public ResponseEntity<?> bookshelf(@AuthenticationPrincipal UserDetails userDetails) {
        List<BookshelfDTO> data = memberService.bookshelf(userDetails.getUsername());
        log.info("Identifier={} : 사용자 책장 목록 조회 성공", userDetails.getUsername());
        return ResponseEntity.ok(TaletApiResponse.success(data));
    }

    @Operation(summary = "책 읽기", description = "책 id를 받아서 currentPage를 업데이트 하는 api", security = @SecurityRequirement(name = "bearerAuth"))
    @ReadBook
    @PostMapping("/read")
    public ResponseEntity<?> read(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ReadBookDTO dto) {
        memberService.readBook(dto, userDetails.getUsername());
        log.info("Identifier={}: 현재 읽고 있는 페이지 추가", userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

}
