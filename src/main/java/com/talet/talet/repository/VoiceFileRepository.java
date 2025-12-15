package com.talet.talet.repository;

import com.talet.talet.entity.VoiceFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoiceFileRepository extends JpaRepository<VoiceFile, Object> {
    List<VoiceFile> findByMember_MemberId(String memberId);
    VoiceFile findById(Long id);
}
