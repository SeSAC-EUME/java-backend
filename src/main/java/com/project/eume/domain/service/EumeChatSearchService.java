package com.project.eume.domain.service;

import com.project.eume.domain.entity.EumeChatContent;
import com.project.eume.domain.entity.EumeChatList;
import com.project.eume.domain.repository.EumeChatContentRepository;
import com.project.eume.domain.repository.EumeChatListRepository;
import com.project.eume.exceptions.errorcode.EumeChatErrorCode;
import com.project.eume.exceptions.exception.EumeChatException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class EumeChatSearchService {
    private final EumeChatListRepository eumeChatListRepository;
    private final EumeChatContentRepository eumeChatContentRepository;

    public Optional<EumeChatList> findByEumeUserIdOrOptional(Long eumeUserId) {
        return eumeChatListRepository.findByEumeUserId(eumeUserId);
    }

    public EumeChatList findByEumeUserId(Long eumeUserId) {
        return eumeChatListRepository.findByEumeUserId(eumeUserId)
                .orElseThrow(() -> new EumeChatException(EumeChatErrorCode.CHAT_LIST_NOT_FOUND));
    }

    public EumeChatList findById(Long chatListId) {
        return eumeChatListRepository.findById(chatListId)
                .orElseThrow(() -> new EumeChatException(EumeChatErrorCode.CHAT_LIST_NOT_FOUND));
    }

    public Page<EumeChatContent> findContentsByEumeChatListId(Long chatListId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return eumeChatContentRepository.findByEumeChatListIdOrderByCreatedAtDesc(chatListId, pageable);
    }
}
