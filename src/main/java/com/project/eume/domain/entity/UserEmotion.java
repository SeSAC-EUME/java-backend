package com.project.eume.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEmotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eume_user_id", nullable = false)
    private EumeUser eumeUser;

    private Integer depressionScore;

    private Integer anxietyScore;

    private Integer stressScore;

    private Integer emotionScore;

    @Column(columnDefinition = "jsonb")
    private String keywords;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime analysisDate;

    private String modelVersion;
}
