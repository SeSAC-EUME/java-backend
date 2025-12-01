package com.project.eume.domain.service;

import com.project.eume.domain.entity.UserChatContent;
import com.project.eume.domain.entity.UserChatList;
import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.repository.UserChatContentRepository;
import com.project.eume.domain.repository.UserChatListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserChatRegisterService {
    private final UserChatListRepository userChatListRepository;
    private final UserChatContentRepository userChatContentRepository;

    @Transactional
    public UserChatList createChatRoom(EumeUser eumeUser) {
        UserChatList chatRoom = UserChatList.ofNewChatRoom(eumeUser);
        return userChatListRepository.save(chatRoom);
    }

    @Transactional
    public UserChatContent saveMessage(UserChatList chatList, EumeUser sender, String messageType, String messageContent) {
        UserChatContent content = UserChatContent.ofNewMessage(chatList, sender, messageType, messageContent);
        return userChatContentRepository.save(content);
    }
}
