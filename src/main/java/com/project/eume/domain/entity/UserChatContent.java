package com.project.eume.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserChatContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_chat_list_id", nullable = false)
    private UserChatList userChatList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eume_user_id", nullable = false)
    private EumeUser eumeUser;

    @Column(nullable = false)
    private String messageType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String messageContent;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 새로운 메시지 생성을 위한 정적 팩토리 메서드
     */
    public static UserChatContent ofNewMessage(
            UserChatList chatList,
            EumeUser sender,
            String messageType,
            String messageContent
    ) {
        return UserChatContent.builder()
                .userChatList(chatList)
                .eumeUser(sender)
                .messageType(messageType)
                .messageContent(messageContent)
                .build();
    }
}
