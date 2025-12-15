package com.talet.talet.repository;

import com.talet.talet.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Object> {
    boolean existsByIdentifier(String identifier);
    Member findByIdentifier(String identifier);
    Member findByMemberId(String memberId);
}
