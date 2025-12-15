package com.talet.talet.controller;

import com.talet.talet.docs.fairytalebook.Book;
import com.talet.talet.docs.fairytalebook.BookDetail;
import com.talet.talet.docs.fairytalebook.LookingBook;
import com.talet.talet.dto.AddBookRequestDTO;
import com.talet.talet.dto.BookDetailDTO;
import com.talet.talet.dto.BookResponseDTO;
import com.talet.talet.dto.LookingBookDTO;
import com.talet.talet.service.FairyTaleBookService;
import com.talet.talet.util.TaletApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class FairyTaleBookController {

    private final FairyTaleBookService fairyTaleBookService;

    // 책 전체 리스트 보여주기 - 태그, 썸네일, 제목, 내용 요약
    @Operation(summary = "책 전체 리스트", description = "책 전체 리스트 보여주기 api - 태그, 썸네일, 제목, 내용 요약")
    @Book
    @GetMapping("/all")
    public ResponseEntity<?> getAllBook() {
        List<BookResponseDTO> books = fairyTaleBookService.findAll();
        log.info("책 전체 리스트 조회 성공");
        return ResponseEntity.ok(TaletApiResponse.success(books));
    }

    // 책 상세보기
    @Operation(summary = "책 상세보기", description = "책 상세보기 api({id}에는 책 ID 필수)", security = {@SecurityRequirement(name = "bearerAuth")})
    @BookDetail
    @GetMapping("/find/{id}")
    public ResponseEntity<?> getBookById(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
        BookDetailDTO dto = fairyTaleBookService.findById(id, userDetails.getUsername());
        log.info("Identifier={} : 사용자, Book={} : 책 단건 조회 성공", userDetails.getUsername(), dto.getName());
        // 아 그리고 여기에서 북 카운팅 1 증가 시켜야 되는데
        fairyTaleBookService.plusCount(id);
        log.info("Book={} : 책 랭킹 카운트 증가", dto.getName());
        return ResponseEntity.ok(TaletApiResponse.success(dto));
    }

    // 책 태그로 리스트 찾기
    @Operation(summary = "책 태그로 리스트 찾기", description = "책 태그로 리스트 찾기 api({tag}에는 관련 태그 필수)")
    @Book
    @GetMapping("/find/tag/{tag}")
    public ResponseEntity<?> getBookByTag(@PathVariable String tag) {
        List<BookResponseDTO> books = fairyTaleBookService.findByTag(tag);
        log.info("BookTag={} : 책 리스트 조회 성공", tag);
        return ResponseEntity.ok(TaletApiResponse.success(books));
    }

    // 책 랭킹순 - 썸네일, 제목
    @Operation(summary = "책 랭킹순 리스트", description = "책 랭킹순 리스트 api - 썸네일, 제목")
    @Book
    @GetMapping("/ranking")
    public ResponseEntity<?> getBookRanking() {
        List<BookResponseDTO> books = fairyTaleBookService.findAllByOrderByCountDesc();
        log.info("책 랭킹순 리스트 조회 성공");
        return ResponseEntity.ok(TaletApiResponse.success(books));
    }

    // 둘러보기
    @Operation(summary = "둘러보기", description = "책 둘러보기 탭 api", security = {@SecurityRequirement(name = "bearerAuth")})
    @LookingBook
    @GetMapping("/look")
    public ResponseEntity<?> look(@AuthenticationPrincipal UserDetails userDetails) {
        // 여기에 랭킹도 넣어야 되는데...
        // 우선은 랭킹순으로 조회해서 데이터를 넘겨주는거로 수정
        List<LookingBookDTO> data = fairyTaleBookService.lookingBooks(userDetails.getUsername());
        log.info("둘러보기 리스트 조회 성공");
        return ResponseEntity.ok(TaletApiResponse.success(data));
    }

    @Hidden
    @PostMapping("/add")
    public TaletApiResponse bookAdd(@RequestBody AddBookRequestDTO addBookRequestDTO) {
        fairyTaleBookService.addBook(addBookRequestDTO);
        return TaletApiResponse.successMessage("데이터 저장 성공");
    }

    @Hidden
    @PostMapping("/add/thumbnail")
    public TaletApiResponse<Map<String, String>> addThumbnail(@RequestPart("file") MultipartFile file, @RequestPart("folder") String folder) {
        Map<String, String> map = new HashMap<>();
        String result = fairyTaleBookService.addThumbnail(file, folder);
        map.put("url", result);
        return TaletApiResponse.success(map);
    }

    @Hidden
    @PostMapping("/add/still")
    public TaletApiResponse<Map<String, String>> addStillImages(@RequestPart("file") MultipartFile file, @RequestPart("folder") String folder) {
        Map<String, String> map = new HashMap<>();
        String result = fairyTaleBookService.addStillImage(file, folder);
        map.put("url", result);
        return TaletApiResponse.success(map);
    }

}
