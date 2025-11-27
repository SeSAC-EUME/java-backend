package com.project.eume.domain.repository;

import com.project.eume.domain.entity.UserChatContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChatContentRepository extends JpaRepository<UserChatContent, Long> {
    Page<UserChatContent> findByUserChatListIdOrderByCreatedAtDesc(Long userChatListId, Pageable pageable);
}
