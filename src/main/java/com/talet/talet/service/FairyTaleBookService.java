package com.talet.talet.service;

import com.talet.talet.dto.AddBookRequestDTO;
import com.talet.talet.dto.BookDetailDTO;
import com.talet.talet.dto.BookResponseDTO;
import com.talet.talet.dto.LookingBookDTO;
import com.talet.talet.entity.BookStillImage;
import com.talet.talet.entity.FairyTaleBook;
import com.talet.talet.entity.Member;
import com.talet.talet.exception.CustomException;
import com.talet.talet.repository.BookMarkRepository;
import com.talet.talet.repository.FairyTaleBookRepository;
import com.talet.talet.repository.MemberRepository;
import com.talet.talet.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FairyTaleBookService {
    private final FairyTaleBookRepository fairyTaleBookRepository;
    private final JWTTokenUtil jwtTokenUtil;
    private final MemberRepository memberRepository;
    private final BookMarkRepository bookMarkRepository;
    private final BookReader bookReader;

    @Value("${storage.books-images-dir}")
    private String imageDir;
    @Value("${storage.images-url-prefix}")
    private String imagePath;
    @Value("${storage.book-root-dir}")
    private String fileDir;

    // 전체 책 리스트
    public List<BookResponseDTO> findAll() {
        List<FairyTaleBook> fairyTaleBooks = fairyTaleBookRepository.findAll();
        List<BookResponseDTO> bookResponseDTOList = new ArrayList<>();
        fairyTaleBooks.forEach(book -> {
            String plot = getPlot(book.getEnglishName());
            System.out.println(plot);
            BookResponseDTO dto = BookResponseDTO.builder()
                    .id(book.getId())
                    .name(book.getName())
                    .thumbnail(book.getThumbnail())
                    .tags(BookTag.toApiList(book.getTags()))
                    .plot(plot).build();
            bookResponseDTOList.add(dto);
        });
        log.info("전체 책 리스트 조회");
        return bookResponseDTOList;
    }

    public List<LookingBookDTO> lookingBooks(String identifier) {
        Member member = memberRepository.findByIdentifier(identifier);
        List<FairyTaleBook> books = findAllByOrderByCountDescFromFairyTaleBook();
        List<LookingBookDTO> lookingBooks = new ArrayList<>();
        books.forEach(book -> {
            Map<String, String> shorts = getShorts(book.getEnglishName(), member.getNativeLanguages());
            boolean bookmark = bookMarkRepository.existsByMemberAndBook(member, book);
            LookingBookDTO dto = LookingBookDTO.builder()
                    .id(book.getId())
                    .name(book.getName())
                    .thumbnail(book.getThumbnail())
                    .tags(BookTag.toApiList(book.getTags()))
                    .shorts(shorts)
                    .bookmark(bookmark).build();
            lookingBooks.add(dto);
        });
        log.info("둘러보기 리스트 정리");
        return lookingBooks;
    }

    public List<BookResponseDTO> findByTag(String tag) {
        List<FairyTaleBook> fairyTaleBooks = fairyTaleBookRepository.findByTagsContaining(tag);
        List<BookResponseDTO> bookResponseDTOList = new ArrayList<>();
        fairyTaleBooks.forEach(book -> {
            String plot = getPlot(book.getEnglishName());
            BookResponseDTO dto = BookResponseDTO.builder()
                    .id(book.getId())
                    .name(book.getName())
                    .thumbnail(book.getThumbnail())
                    .tags(BookTag.toApiList(book.getTags()))
                    .plot(plot).build();
            bookResponseDTOList.add(dto);
        });
        log.info("BookTag={} : 책 리스트 조회", tag);
        return bookResponseDTOList;
    }

    public List<BookResponseDTO> findAllByOrderByCountDesc() {
        List<FairyTaleBook> fairyTaleBooks = fairyTaleBookRepository.findAllByOrderByCountDesc();
        List<BookResponseDTO> bookResponseDTOList = new ArrayList<>();
        fairyTaleBooks.forEach(book -> {
            String plot = getPlot(book.getEnglishName());
            BookResponseDTO dto = BookResponseDTO.builder()
                    .id(book.getId())
                    .name(book.getName())
                    .thumbnail(book.getThumbnail())
                    .tags(BookTag.toApiList(book.getTags()))
                    .plot(plot).build();
            bookResponseDTOList.add(dto);
        });
        log.info("랭킹순 책 리스트 조회");
        return bookResponseDTOList;
    }

    public List<FairyTaleBook> findAllByOrderByCountDescFromFairyTaleBook() {
        return fairyTaleBookRepository.findAllByOrderByCountDesc();
    }

    public BookDetailDTO findById(String id, String identifier) {
        Member member = memberRepository.findByIdentifier(identifier);
        FairyTaleBook book = fairyTaleBookRepository.findById(id);
        Map<String, String> shorts = getShorts(book.getEnglishName(), member.getNativeLanguages());
        Map<String, String> plots = getPlots(book.getEnglishName(), member.getNativeLanguages());
        boolean bookmark = bookMarkRepository.existsByMemberAndBook(member, book);
        List<String> stillImages = new ArrayList<>();
        List<BookStillImage> bookStillImages = book.getStillImages();
        bookStillImages.forEach(bookStillImage -> {
            stillImages.add(bookStillImage.getImage());
        });
        log.info("Identifier={} : 사용자, Book={} : 책", identifier, book.getName());
        return BookDetailDTO.builder()
                .id(book.getId())
                .name(book.getName())
                .thumbnail(book.getThumbnail())
                .stillImages(stillImages)
                .tags(BookTag.toApiList(book.getTags()))
                .shorts(shorts)
                .plots(plots)
                .bookmark(bookmark).build();
    }

    public void plusCount(String id) {
        FairyTaleBook book = fairyTaleBookRepository.findById(id);
        book.setCount(book.getCount() + 1);
        log.info("Book={} : 책 랭킹 카운트 1 증가", book.getName());
        fairyTaleBookRepository.save(book);
    }

    public Map<String, String> getShorts(String bookName, List<LanguageEnum> languages) {
        Map<String, String> shorts = new HashMap<>();
        languages.forEach(language -> {
            String lang = "short_" + getLanguageCode(language);
            String shortData = null;
            try {
                 shortData = bookReader.read(bookName, lang);
            } catch (Exception e) {
                log.warn("Book={}-{} : 책 한줄 요약 조회 실패",  bookName,  lang);
                throw new CustomException(ErrorEnum.COMMON_INTERNAL_ERROR);
            }
            shorts.put(lang, shortData);
            log.info("Book={}-{} : 책 한줄 요약 조회 성공",  bookName,  lang);
        });
        return shorts;
    }

    public Map<String, String> getPlots(String bookName, List<LanguageEnum> languages) {
        Map<String, String> plots = new HashMap<>();
        languages.forEach(language -> {
            String lang = "plot_" + getLanguageCode(language);
            String plotData = null;
            try {
                plotData = bookReader.read(bookName, lang);
            } catch (Exception e) {
                log.warn("Book={}-{} : 책 내용 요약 조회 실패",  bookName,  lang);
                throw new CustomException(ErrorEnum.COMMON_INTERNAL_ERROR);
            }
            plots.put(lang, plotData);
            log.info("Book={}-{} : 책 내용 요약 조회 성공",  bookName,  lang);
        });
        return plots;
    }

    public String getPlot(String bookName) {
        String lang = "plot_ko";
        String plotData = null;
        try {
            plotData = bookReader.read(bookName, lang);
        } catch (Exception e) {
            log.warn("Book={}-ko : 책 내용 요약 조회 실패",  bookName);
            throw new CustomException(ErrorEnum.COMMON_INTERNAL_ERROR);
        }
        log.info("Book={}-ko : 책 내용 요약 조회 성공",  bookName);
        return plotData;
    }

    public String getContent(String bookName) {
        String data = null;
        try {
            data = bookReader.read(bookName, "content");
        } catch (Exception e) {
            log.warn("Book={} : 책 내용 조회 실패",  bookName);
            throw new CustomException(ErrorEnum.COMMON_INTERNAL_ERROR);
        }
        log.info("Book={} : 책 내용 조회 성공",  bookName);
        return data;
    }

    public FairyTaleBook getBook(String bookId) {
        return fairyTaleBookRepository.findById(bookId);
    }

    // 책 썸네일 추가
    public String addThumbnail(MultipartFile file, String folderName) {
        String folderPath = imageDir + "/" + folderName;
        if (!mkdir(folderPath)) {
            return null;
        }
        String ext = resolveExtension(file);
        if (ext == null) {
            // 이미지가 아니면 거절하거나 .bin 같은 기본값을 줄 수 있음. 여기선 거절.
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
        String fileName = String.format("%s-%s-%s%s", folderName, "thumbnail", UUID.randomUUID(), ext);
        Path filePath = Paths.get(folderPath, fileName);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (Exception e) {
            return null;
        }
        return imagePath + "books/" + folderName + "/" + fileName;
    }

    // 책 스틸컷 추가
    public String addStillImage(MultipartFile file, String folderName) {
        String folderPath = imageDir + "/" + folderName;
        if (!mkdir(folderPath)) {
            return null;
        }
        String ext = resolveExtension(file);
        if (ext == null) {
            // 이미지가 아니면 거절하거나 .bin 같은 기본값을 줄 수 있음. 여기선 거절.
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
        String fileName = String.format("%s-%s-%s%s", folderName, "stillImage", UUID.randomUUID(), ext);
        Path filePath = Paths.get(folderPath, fileName);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (Exception e) {
            return null;
        }
        return imagePath + "books/" + folderName + "/" + fileName;
    }

    // 책 추가
    public void addBook(AddBookRequestDTO addBookRequestDTO) {
        FairyTaleBook fairyTaleBook = new FairyTaleBook();
        fairyTaleBook.setName(addBookRequestDTO.getName());
        fairyTaleBook.setEnglishName(addBookRequestDTO.getEnglishName());
        fairyTaleBook.setCount(0);
        fairyTaleBook.setTags(BookTag.fromList(addBookRequestDTO.getTags()));
        fairyTaleBook.setThumbnail(addBookRequestDTO.getThumbnail());
        fairyTaleBook.setUnitCount(0);
        fairyTaleBook.setFilePath(fileDir + "/" + addBookRequestDTO.getEnglishName());
        // ✅ 여기부터가 핵심 (Map -> BookStillImage 여러 개 생성)
        Map<String, String> stillMap = addBookRequestDTO.getStillImages();
        if (stillMap != null && !stillMap.isEmpty()) {

            // 1) key(인덱스) 오름차순 정렬
            List<Map.Entry<String, String>> entries = new ArrayList<>(stillMap.entrySet());
            entries.sort((a, b) -> Integer.compare(
                    Integer.parseInt(a.getKey()),
                    Integer.parseInt(b.getKey())
            ));

            // 2) 정렬된 순서대로 BookStillImage 만들어서 book에 추가
            for (Map.Entry<String, String> e : entries) {
                int orderIndex = Integer.parseInt(e.getKey()); // "1" -> 1
                String url = e.getValue();

                BookStillImage img = new BookStillImage();
                img.setBook(fairyTaleBook);            // ✅ 이 이미지가 어떤 책 것인지 연결
                img.setImage(url);            // ✅ url 저장
                img.setOrderIndex(orderIndex);// ✅ 1,2,3...

                fairyTaleBook.getStillImages().add(img); // ✅ book이 가진 이미지 목록에 추가
            }
        }
        fairyTaleBookRepository.save(fairyTaleBook);
    }

    public boolean mkdir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    // 언어
    public String getLanguageCode(LanguageEnum lang) {
        return switch (lang) {
            case KOREAN -> "ko";
            case ENGLISH -> "en";
            case JAPANESE -> "ja";
            case CHINESE -> "ch";
            case VIETNAMESE -> "vi";
            case THAI -> "th";
        };
    }

    private String resolveExtension(MultipartFile file) {
        // 1) original filename에서 시도
        String original = file.getOriginalFilename();
        if (original != null) {
            int dot = original.lastIndexOf('.');
            if (dot >= 0 && dot < original.length() - 1) {
                String ext = original.substring(dot).toLowerCase(); // ".png"
                if (isImageExt(ext)) return ext;
            }
        }
        // 2) content-type에서 시도
        String ct = file.getContentType();
        if (ct != null) {
            String byCt = mapContentTypeToExt(ct);
            if (byCt != null) return byCt;
        }
        return null;
    }

    private boolean isImageExt(String ext) {
        return ext.equals(".png") || ext.equals(".jpg") || ext.equals(".jpeg")
                || ext.equals(".gif") || ext.equals(".webp") || ext.equals(".bmp");
    }

    private String mapContentTypeToExt(String ct) {
        Map<String, String> map = Map.of(
                "image/png", ".png",
                "image/jpeg", ".jpg",
                "image/jpg", ".jpg",
                "image/gif", ".gif",
                "image/webp", ".webp",
                "image/bmp", ".bmp"
        );
        return map.get(ct.toLowerCase());
    }

}
