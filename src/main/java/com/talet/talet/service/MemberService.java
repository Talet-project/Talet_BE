package com.talet.talet.service;

import com.talet.talet.dto.*;
import com.talet.talet.entity.BookMark;
import com.talet.talet.entity.FairyTaleBook;
import com.talet.talet.entity.Member;
import com.talet.talet.entity.ReadingBook;
import com.talet.talet.exception.CustomException;
import com.talet.talet.repository.BookMarkRepository;
import com.talet.talet.repository.FairyTaleBookRepository;
import com.talet.talet.repository.MemberRepository;
import com.talet.talet.repository.ReadingBookRepository;
import com.talet.talet.util.ErrorEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BookMarkRepository bookMarkRepository;
    private final ReadingBookRepository readingBookRepository;
    private final FairyTaleBookRepository fairyTaleBookRepository;
    @Value("${storage.users-images-dir}")
    private String usersImagesDir;
    @Value("${storage.public-base-url}")
    private String publicBaseUrl;

    public MemberResponseDTO getUserInfo(String identifier) {
        Member member = memberRepository.findByIdentifier(identifier);
        log.info("Identifier={} : 사용자 정보 조회", identifier);
        return MemberResponseDTO.builder()
                .nickname(member.getName())
                .gender(member.getGender())
                .birthday(member.getBirthDate())
                .profileImage(publicBaseUrl + member.getProfileImage())
                .languages(member.getNativeLanguages())
                .build();
    }

    public MemberResponseDTO memberUpdate(String identifier, MemberRequestDTO memberRequestDTO) {
        Member member = memberRepository.findByIdentifier(identifier);
        member.setName(memberRequestDTO.getNickname());
        member.setGender(memberRequestDTO.getGender());
        member.setBirthDate(memberRequestDTO.getBirthday());
        member.setNativeLanguages(memberRequestDTO.getLanguages());
        Member updateMember = memberRepository.save(member);
        log.info("Identifier={} : 사용자 정보 수정", identifier);
        return MemberResponseDTO.builder()
                .nickname(updateMember.getName())
                .gender(updateMember.getGender())
                .birthday(updateMember.getBirthDate())
                .languages(updateMember.getNativeLanguages())
                .build();
    }

    public MemberResponseDTO memberProfileUpdate(String identifier, MultipartFile profile) {
        Member member = memberRepository.findByIdentifier(identifier);
        deleteImage(member);
        String newPath = addProfile(identifier, profile);
        member.setProfileImage(newPath);
        Member updateMember = memberRepository.save(member);
        log.info("Identifier={} : 사용자 프로필사진 수정", identifier);
        return MemberResponseDTO.builder()
                .profileImage(publicBaseUrl + newPath).build();
    }

    public void deleteImage(Member member) {
        String profileImage = member.getProfileImage();
        if (profileImage == null || profileImage.isBlank()) {
            return;
        }

        // DB에는 "/images/users/{identifier}/{fileName}" 형태로 들어있다고 가정
        final String prefix = "/images/users/";
        if (!profileImage.startsWith(prefix)) {
            log.warn("Identifier={} : 예상치 못한 프로필 경로 형식: {}",
                    member.getIdentifier(), profileImage);
            return;
        }

        // "users/{identifier}/{fileName}" 부분만 추출
        String subPath = profileImage.substring(prefix.length()); // {identifier}/{fileName}

        // 실제 파일 시스템 경로: usersImagesDir + "/" + {identifier}/{fileName}
        Path realPath = Paths.get(usersImagesDir, subPath); // usersImagesDir는 .../images/users

        try {
            Files.deleteIfExists(realPath);
            log.info("Identifier={} : 사용자 프로필사진 삭제 완료 ({})",
                    member.getIdentifier(), realPath);
        } catch (IOException e) {
            log.warn("Identifier={} : 사용자 프로필사진 삭제 실패 ({})",
                    member.getIdentifier(), realPath, e);
        }
    }

    private String addProfile(String identifier, MultipartFile profile) {
        String originalFilename = profile.getOriginalFilename();
        if (originalFilename == null) {
            log.warn("프로필 사진 업로드 실패: 파일 이름 없음");
            throw new CustomException(ErrorEnum.COMMON_INTERNAL_ERROR);
        }

        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + ext;

        // 실제 디렉토리: /.../images/users/{identifier}
        Path dirPath = Paths.get(usersImagesDir, identifier);
        Path filePath = dirPath.resolve(uniqueFilename);

        try {
            // 디렉토리 없으면 생성
            Files.createDirectories(dirPath);
            // 파일 저장
            Files.copy(profile.getInputStream(), filePath);
        } catch (IOException e) {
            log.warn("Identifier={} : 프로필 사진 업로드 실패 ({})",
                    identifier, filePath, e);
            throw new CustomException(ErrorEnum.COMMON_INTERNAL_ERROR);
        }

        log.info("Identifier={} : 프로필 사진 업로드 성공 ({})", identifier, filePath);

        // ✅ DB에는 URL 기준 상대 경로만 저장
        // 실제 URL은 client에서 baseUrl("http://localhost:8080") + 이 값으로 사용
        return "/images/users/" + identifier + "/" + uniqueFilename;
    }

    // 찜 목록
    public List<BookMarkResponseDTO> getBookMark(String identifier) {
        Member member = memberRepository.findByIdentifier(identifier);
        List<FairyTaleBook> bookmarkList = bookMarkRepository.findBooksBookmarkedByMember(member.getMemberId());
        List<BookMarkResponseDTO> bookMarkResponseDTOList = new ArrayList<>();
        bookmarkList.forEach(book -> {
            BookMarkResponseDTO dto = BookMarkResponseDTO.builder()
                    .bookId(book.getId())
                    .title(book.getName())
                    .thumbnail(book.getThumbnail())
                    .build();
            bookMarkResponseDTOList.add(dto);
        });
        log.info("Identifier={} : 사용자 찜 목록 조회", identifier);
        return bookMarkResponseDTOList;
    }

    public List<BookshelfDTO> bookshelf(String identifier) {
        Member member = memberRepository.findByIdentifier(identifier);
        Map<String, BookshelfDTO> bookshelfDTOMap = new HashMap<String, BookshelfDTO>();
        List<ReadingBook> readingBookList = readingBookRepository.findReadingBookByMemberId(member.getMemberId());
        List<BookMark> bookMarkList = bookMarkRepository.findBookMarkByMemberId(member.getMemberId());
        for (BookMark bookMark : bookMarkList) {
            BookshelfDTO dto = new BookshelfDTO();
            FairyTaleBook book = bookMark.getBook();
            dto.setBookId(book.getId());
            dto.setBookName(book.getName());
            dto.setThumbnail(book.getThumbnail());
            dto.setTotalPage(book.getTotalPage());
            dto.setLiked(true);
            bookshelfDTOMap.put(book.getId(), dto);
        }
        for (ReadingBook readingBook : readingBookList) {
            FairyTaleBook book = readingBook.getBook();
            if (bookshelfDTOMap.get(book.getId()) != null) {
                BookshelfDTO dto = bookshelfDTOMap.get(book.getId());
                dto.setCurrentPage(readingBook.getCurrentPage());
                bookshelfDTOMap.put(book.getId(), dto);
                continue;
            }
            BookshelfDTO dto = new BookshelfDTO();
            dto.setBookId(book.getId());
            dto.setBookName(book.getName());
            dto.setThumbnail(book.getThumbnail());
            dto.setTotalPage(book.getTotalPage());
            dto.setCurrentPage(readingBook.getCurrentPage());
            dto.setLiked(bookMarkRepository.existsByMemberAndBook(member, book));
            bookshelfDTOMap.put(book.getId(), dto);
        }

        return new ArrayList<>(bookshelfDTOMap.values());
    }

    public void bookmark(BookmarkRequestDTO bookmarkRequestDTO, String identifier) {
        Member member = memberRepository.findByIdentifier(identifier);
        FairyTaleBook book = fairyTaleBookRepository.findById(bookmarkRequestDTO.getBookId());
        BookMark bookMark = bookMarkRepository.findByMember_MemberIdAndBook_Id(member.getMemberId(), bookmarkRequestDTO.getBookId());
        if (bookMark == null) {
            bookMark = new BookMark();
            bookMark.setMember(member);
            bookMark.setBook(book);
            bookMarkRepository.save(bookMark);
        } else {
            bookMarkRepository.delete(bookMark);
        }
    }

}
