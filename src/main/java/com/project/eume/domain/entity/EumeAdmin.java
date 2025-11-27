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
@Table(name = "eume_admin")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EumeAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String adminLoginId;

    @Column(nullable = false)
    private String adminPw;

    @Column(nullable = false)
    private String adminName;

    @Column(nullable = false, unique = true)
    private String adminEmail;

    @Column
    private String adminPhone;

    /**
     * ACTIVE : 활성화 (default)
     * DEACTIVATED : 비활성화
     */
    @Column
    private String adminStatus;

    @OneToOne
    @JoinColumn(name = "sigungu_id")
    private Sigungu sigungu;

    @Column
    private LocalDateTime lastLoginDate;

    @Column(nullable = false)
    private Integer loginFailCount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
