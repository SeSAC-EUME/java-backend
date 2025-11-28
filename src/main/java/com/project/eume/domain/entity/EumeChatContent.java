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
public class EumeChatContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eume_chat_list_id", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private EumeChatList eumeChatList;

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
     * 사용자 메시지 생성을 위한 정적 팩토리 메서드
     */
    public static EumeChatContent ofUserMessage(EumeChatList chatList, EumeUser user, String content) {
        return EumeChatContent.builder()
                .eumeChatList(chatList)
                .eumeUser(user)
                .messageType("USER")
                .messageContent(content)
                .build();
    }

    /**
     * AI(Eume) 메시지 생성을 위한 정적 팩토리 메서드
     */
    public static EumeChatContent ofEumeMessage(EumeChatList chatList, EumeUser user, String content) {
        return EumeChatContent.builder()
                .eumeChatList(chatList)
                .eumeUser(user)
                .messageType("EUME")
                .messageContent(content)
                .build();
    }
}
