package com.talet.talet.repository;

import com.talet.talet.entity.FairyTaleBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FairyTaleBookRepository extends JpaRepository<FairyTaleBook, Object> {
    FairyTaleBook findById(String id);

    List<FairyTaleBook> findByTagsContaining(String tag);

    // 클릭 수 내림차순 정렬
    List<FairyTaleBook> findAllByOrderByCountDesc();

    // (옵션) 상위 n개만 가져오고 싶으면
//    List<FairyTaleBook> findTop10ByOrderByCountDesc();
}
