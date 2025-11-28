package com.project.eume.domain.entity;

import com.project.eume.domain.enums.ChatStatus;
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
public class EumeChatList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eume_user_id", nullable = false)
    private EumeUser eumeUser;

    /**
     * ACTIVE : 활성화 (default)
     * DEACTIVATED : 비활성화
     */
    @Column(nullable = false)
    private ChatStatus chatStatus;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 새로운 EumeChatList 생성을 위한 정적 팩토리 메서드
     */
    public static EumeChatList ofNewChatList(EumeUser eumeUser) {
        return EumeChatList.builder()
                .eumeUser(eumeUser)
                .chatStatus(ChatStatus.ACTIVE)
                .build();
    }
}
