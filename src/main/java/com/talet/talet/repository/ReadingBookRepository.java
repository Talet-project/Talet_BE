package com.talet.talet.repository;

import com.talet.talet.entity.BookMark;
import com.talet.talet.entity.Member;
import com.talet.talet.entity.ReadingBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReadingBookRepository extends JpaRepository<ReadingBook, Long> {
    @Query("select rb from ReadingBook rb where rb.member.memberId = :memberId")
    List<ReadingBook> findReadingBookByMemberId(@Param("memberId") String memberId);
}
