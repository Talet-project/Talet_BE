package com.talet.talet.repository;

import com.talet.talet.entity.BookMark;
import com.talet.talet.entity.FairyTaleBook;
import com.talet.talet.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookMarkRepository extends JpaRepository<BookMark, Object> {
    // 특정 사용자와 책으로 북마크가 존재하는지 체크
    boolean existsByMemberAndBook(Member member, FairyTaleBook book);
    // 만약 memberId, bookId로 찾으려면
    boolean existsByMember_MemberIdAndBook_Id(String memberId, String bookId);

    @Query("select bm.book from BookMark bm where bm.member.memberId = :memberId")
    List<FairyTaleBook> findBooksBookmarkedByMember(@Param("memberId") String memberId);

    @Query("select bm from BookMark bm where bm.member.memberId = :memberId")
    List<BookMark> findBookMarkByMemberId(@Param("memberId") String memberId);

    BookMark findByMember_MemberIdAndBook_Id(@Param("memberId") String memberId, @Param("bookId") String bookId);
}
