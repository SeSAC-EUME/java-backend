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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserChatService {
    private final UserChatSearchService userChatSearchService;
    private final EumeUserSearchService eumeUserSearchService;
    private final WebClient webClient;

    @Value("${n8n.webhook.user-chat.url}")
    private String n8nWebhookUrl;

    public Page<UserChatContent> getContents(String userEmail, Long chatListId, int page, int size) {
        EumeUser user = eumeUserSearchService.findByEmail(userEmail);
        UserChatList chatList = userChatSearchService.findById(chatListId);
        validateChatRoomAccess(chatList, user);

        return userChatSearchService.findContentsByChatListId(chatListId, page, size);
    }

    public Mono<UserChatContentCreateResponse> sendMessage(
            String userEmail,
            Long chatListId,
            UserChatContentCreateRequest request
    ) {
        // 1. 사용자 조회
        EumeUser user = eumeUserSearchService.findByEmail(userEmail);

        // 2. 채팅 목록 조회 및 권한 확인
        UserChatList chatList = userChatSearchService.findById(chatListId);
        validateChatRoomAccess(chatList, user);

        String userMessage = request.messageContent();

        // 3. n8n 웹훅 호출하여 AI 응답 받기 (비동기)
        return callN8nWebhook(chatList, user, userMessage)
                .map(eumeResponse -> UserChatContentCreateResponse.fromAiResponse(userMessage, eumeResponse));
    }

    public void validateChatRoomAccess(UserChatList chatList, EumeUser user) {
        if (!chatList.getEumeUser().getId().equals(user.getId())) {
            throw new UserChatException(UserChatErrorCode.FORBIDDEN_ACCESS);
        }
    }

    private Mono<String> callN8nWebhook(UserChatList chatList, EumeUser user, String message) {
        if (n8nWebhookUrl == null || n8nWebhookUrl.isBlank()) {
            log.warn("n8n webhook URL is not configured. Returning default response.");
            return Mono.just("안녕하세요! 현재 AI 서비스가 설정되지 않았습니다.");
        }

        return webClient.post()
                .uri(n8nWebhookUrl)
                .bodyValue(Map.of(
                        "message", message,
                        "sessionId", chatList.getId().toString(),
                        "userId", user.getId().toString()
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    if (response != null && response.containsKey("response")) {
                        return response.get("response").toString();
                    }
                    return "AI 응답을 처리할 수 없습니다.";
                })
                .onErrorResume(e -> {
                    log.error("n8n webhook call failed: {}", e.getMessage(), e);
                    return Mono.error(new UserChatException(UserChatErrorCode.N8N_WEBHOOK_ERROR));
                });
    }
}
