package com.project.eume.domain.service;

import com.project.eume.domain.dto.request.UserChatContentCreateRequest;
import com.project.eume.domain.dto.response.UserChatContentCreateResponse;
import com.project.eume.domain.entity.UserChatContent;
import com.project.eume.domain.entity.UserChatList;
import com.project.eume.domain.entity.EumeUser;
import com.project.eume.exceptions.errorcode.UserChatErrorCode;
import com.project.eume.exceptions.exception.UserChatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserChatService {
    private final UserChatSearchService userChatSearchService;
    private final UserChatRegisterService userChatRegisterService;
    private final EumeUserSearchService eumeUserSearchService;

    public Page<UserChatContent> getContents(String userEmail, Long chatListId, int page, int size) {
        EumeUser user = eumeUserSearchService.findByEmail(userEmail);
        UserChatList chatList = userChatSearchService.findById(chatListId);
        validateChatRoomAccess(chatList, user);

        return userChatSearchService.findContentsByChatListId(chatListId, page, size);
    }

    @Transactional
    public UserChatContentCreateResponse sendMessage(
            String userEmail,
            Long chatListId,
            UserChatContentCreateRequest request
    ) {
        EumeUser user = eumeUserSearchService.findByEmail(userEmail);
        UserChatList chatList = userChatSearchService.findById(chatListId);
        validateChatRoomAccess(chatList, user);

        UserChatContent content = userChatRegisterService.saveMessage(
                chatList,
                user,
                request.messageType(),
                request.messageContent()
        );

        return UserChatContentCreateResponse.from(content);
    }

    public void validateChatRoomAccess(UserChatList chatList, EumeUser user) {
        if (!chatList.getEumeUser().getId().equals(user.getId())) {
            throw new UserChatException(UserChatErrorCode.FORBIDDEN_ACCESS);
        }
    }
}
