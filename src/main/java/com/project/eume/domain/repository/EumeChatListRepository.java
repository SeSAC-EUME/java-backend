package com.project.eume.domain.repository;

import com.project.eume.domain.entity.EumeChatList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EumeChatListRepository extends JpaRepository<EumeChatList, Long> {
    Optional<EumeChatList> findByEumeUserId(Long eumeUserId);
    boolean existsByEumeUserId(Long eumeUserId);
}
