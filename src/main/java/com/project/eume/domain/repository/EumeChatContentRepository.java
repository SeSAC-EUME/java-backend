package com.project.eume.domain.repository;

import com.project.eume.domain.entity.EumeChatContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EumeChatContentRepository extends JpaRepository<EumeChatContent, Long> {
    Page<EumeChatContent> findByEumeChatListIdOrderByCreatedAtDesc(Long eumeChatListId, Pageable pageable);
}
