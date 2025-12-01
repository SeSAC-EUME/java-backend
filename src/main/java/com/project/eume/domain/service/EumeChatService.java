package com.project.eume.domain.service;

import com.project.eume.domain.dto.request.EumeChatContentCreateRequest;
import com.project.eume.domain.dto.response.EumeChatContentCreateResponse;
import com.project.eume.domain.entity.EumeChatContent;
import com.project.eume.domain.entity.EumeChatList;
import com.project.eume.domain.entity.EumeUser;
import com.project.eume.exceptions.errorcode.EumeChatErrorCode;
import com.project.eume.exceptions.exception.EumeChatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class EumeChatService {
    private final EumeChatSearchService eumeChatSearchService;
    private final EumeChatRegisterService eumeChatRegisterService;
    private final EumeUserSearchService eumeUserSearchService;
    private final WebClient webClient;

    @Value("${n8n.webhook.eume-chat.url:}")
    private String n8nWebhookUrl;

    @Transactional
    public EumeChatContentCreateResponse sendMessage(
            String userEmail,
            Long chatListId,
            EumeChatContentCreateRequest request
    ) {
        // 1. 사용자 조회
        EumeUser user = eumeUserSearchService.findByEmail(userEmail);

        // 2. 채팅 목록 조회 및 권한 확인
        EumeChatList chatList = eumeChatSearchService.findById(chatListId);
        validateChatListOwnership(chatList, user);

        // 3. 사용자 메시지 저장
        EumeChatContent userContent = eumeChatRegisterService.saveUserMessage(
                chatList, user, request.messageContent()
        );

        // 4. n8n 웹훅 호출하여 AI 응답 받기
        String eumeResponse = callN8nWebhook(userContent);

        // 5. AI 응답 저장
        EumeChatContent eumeContent = eumeChatRegisterService.saveEumeMessage(
                chatList, user, eumeResponse
        );

        return EumeChatContentCreateResponse.from(userContent, eumeContent);
    }

    public void validateChatListOwnership(EumeChatList chatList, EumeUser user) {
        if (!chatList.getEumeUser().getId().equals(user.getId())) {
            throw new EumeChatException(EumeChatErrorCode.FORBIDDEN_ACCESS);
        }
    }

    private String callN8nWebhook(EumeChatContent eumeChatContent) {
        if (n8nWebhookUrl == null || n8nWebhookUrl.isBlank()) {
            log.warn("n8n webhook URL is not configured. Returning default response.");
            return "안녕하세요! 이음이입니다. 현재 AI 서비스가 설정되지 않았습니다.";
        }

        try {
            Map response = webClient.post()
                    .uri(n8nWebhookUrl)
                    .bodyValue(Map.of(
                        "message", eumeChatContent.getMessageContent(),
                        "sessionId", eumeChatContent.getEumeChatList().getId().toString(),
                        "userId", eumeChatContent.getEumeUser().getId().toString()
                        ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("response")) {
                return response.get("response").toString();
            }

            return "AI 응답을 처리할 수 없습니다.";
        } catch (Exception e) {
            log.error("n8n webhook call failed: {}", e.getMessage(), e);
            throw new EumeChatException(EumeChatErrorCode.N8N_WEBHOOK_ERROR);
        }
    }
}
