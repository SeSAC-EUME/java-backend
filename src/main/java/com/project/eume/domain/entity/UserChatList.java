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
public class UserChatList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eume_user_id")
    private EumeUser eumeUser;

    private String roomTitle;

    /**
     * ACTIVE : 활성화 (default)
     * DEACTIVATED : 비활성화
     */
    @Column(nullable = false)
    private String roomStatus;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 새로운 채팅방 생성을 위한 정적 팩토리 메서드
     */
    public static UserChatList ofNewChatRoom(EumeUser eumeUser) {
        return UserChatList.builder()
                .eumeUser(eumeUser)
                .roomStatus("ACTIVE")
                .build();
    }

    /**
     * 채팅 방의 title을 설정하는 메서드
     *
     * @param roomTitle
     */
    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }
}
