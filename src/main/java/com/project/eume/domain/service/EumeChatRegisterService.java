package com.project.eume.domain.service;

import com.project.eume.domain.entity.EumeChatContent;
import com.project.eume.domain.entity.EumeChatList;
import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.repository.EumeChatContentRepository;
import com.project.eume.domain.repository.EumeChatListRepository;
import com.project.eume.exceptions.errorcode.EumeChatErrorCode;
import com.project.eume.exceptions.exception.EumeChatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class EumeChatRegisterService {
    private final EumeChatListRepository eumeChatListRepository;
    private final EumeChatContentRepository eumeChatContentRepository;

    @Transactional
    public EumeChatList createChatList(EumeUser eumeUser) {
        if (eumeChatListRepository.existsByEumeUserId(eumeUser.getId())) {
            throw new EumeChatException(EumeChatErrorCode.CHAT_LIST_ALREADY_EXISTS);
        }

        EumeChatList chatList = EumeChatList.ofNewChatList(eumeUser);
        return eumeChatListRepository.save(chatList);
    }

    @Transactional
    public EumeChatContent saveUserMessage(EumeChatList chatList, EumeUser user, String content) {
        EumeChatContent userContent = EumeChatContent.ofUserMessage(chatList, user, content);
        return eumeChatContentRepository.save(userContent);
    }

    @Transactional
    public EumeChatContent saveEumeMessage(EumeChatList chatList, EumeUser user, String content) {
        EumeChatContent eumeContent = EumeChatContent.ofEumeMessage(chatList, user, content);
        return eumeChatContentRepository.save(eumeContent);
    }
}
