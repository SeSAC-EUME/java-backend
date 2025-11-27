package com.project.eume.domain.service;

import com.project.eume.domain.entity.UserChatContent;
import com.project.eume.domain.entity.UserChatList;
import com.project.eume.domain.repository.UserChatContentRepository;
import com.project.eume.domain.repository.UserChatListRepository;
import com.project.eume.exceptions.errorcode.UserChatErrorCode;
import com.project.eume.exceptions.exception.UserChatException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserChatSearchService {
    private final UserChatListRepository userChatListRepository;
    private final UserChatContentRepository userChatContentRepository;

    public Page<UserChatList> findByEumeUserId(Long eumeUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userChatListRepository.findByEumeUserIdOrderByUpdatedAtDesc(eumeUserId, pageable);
    }

    public UserChatList findById(Long chatListId) {
        return userChatListRepository.findById(chatListId)
                .orElseThrow(() -> new UserChatException(UserChatErrorCode.CHAT_ROOM_NOT_FOUND));
    }

    public Page<UserChatContent> findContentsByChatListId(Long chatListId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userChatContentRepository.findByUserChatListIdOrderByCreatedAtDesc(chatListId, pageable);
    }
}
