package com.project.eume.domain.repository;

import com.project.eume.domain.entity.UserChatList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChatListRepository extends JpaRepository<UserChatList, Long> {
    Page<UserChatList> findByEumeUserIdOrderByUpdatedAtDesc(Long eumeUserId, Pageable pageable);
}
