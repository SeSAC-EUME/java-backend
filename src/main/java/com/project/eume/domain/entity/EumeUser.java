package com.project.eume.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EumeUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String userName;

    private String nickname;

    private String profileImage;

    private String providerId;

    /**
     * ACTIVE : 활성화 (default)
     * DEACTIVATED : 비활성화
     */
    @Column(nullable = false)
    private String userStatus;

    @OneToOne
    @JoinColumn(name = "sigungu_id")
    private Sigungu sigungu;

    private LocalDate birthDate;

    /**
     * M : 남성
     * F : 여성
     */
    private String gender;

    private String phone;

    private LocalDateTime lastLoginDate;

    @Column(nullable = false)
    private Integer loginFailCount = 0;

    /**
     * DEFAULT : 기본 테마 (default)
     * DARK : 다크 테마
     * LIGHT : 라이트 테마
     */
    private String backgroundTheme;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
