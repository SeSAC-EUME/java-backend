package com.project.eume.domain.entity;

import com.project.eume.domain.dto.request.EumeUserUpdateRequest;
import com.project.eume.domain.enums.BackgroundTheme;
import com.project.eume.domain.enums.LoginPlatform;
import com.project.eume.domain.enums.UserStatus;
import com.project.eume.exceptions.errorcode.EumeUserErrorCode;
import com.project.eume.exceptions.exception.EumeUserException;
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
import static java.util.Objects.isNull;

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
    private Integer loginFailCount;

    /**
     * DEFAULT : 기본 테마 (default)
     * DARK : 다크 테마
     * LIGHT : 라이트 테마
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private BackgroundTheme backgroundTheme;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 신규 유저 회원가입 시 User를 생성하여 반환합니다.
     *
     * @param email         요청 이메일
     * @param name          요청 이름
     * @param loginPlatform 요청 로그인 플랫폼
     * @return 생성된 User
     */
    public static EumeUser ofNewRegistration(String email, String name, LoginPlatform loginPlatform) {
        // 필요할 경우, loginPlatform에 따른 추가 로직 구현
        return EumeUser.builder()
            .email(email)
            .userName(name)
            .providerId(loginPlatform.name())
            .userStatus(UserStatus.ACTIVE.name())
            .lastLoginDate(LocalDateTime.now())
            .loginFailCount(0)
            .backgroundTheme(BackgroundTheme.DEFAULT)
            .build();
    }

    /**
     * 사용자 정보 업데이트
     *
     * @param request    업데이트 요청 DTO
     * @param newSigungu 새로운 시군구 정보
     */
    public void update(EumeUserUpdateRequest request, Sigungu newSigungu) {
        if (!isNull(request.userName())) this.userName = request.userName();
        if (!isNull(request.nickName())) this.nickname = request.nickName();
        if (!isNull(request.profileImage())) this.profileImage = request.profileImage();
        if (!isNull(newSigungu)) this.sigungu = newSigungu;
        if (!isNull(request.birthDate())) this.birthDate = request.birthDate();
        if (!isNull(request.gender())) this.gender = request.gender();
        if (!isNull(request.phone())) this.phone = request.phone();
        if (!isNull(request.backgroundTheme())) this.backgroundTheme = request.backgroundTheme();
    }


    /**
     * 계정 비활성화
     */
    public void deactivate() {
        if (UserStatus.DEACTIVATED.name().equals(this.userStatus)) {
            throw new EumeUserException(EumeUserErrorCode.ALREADY_DEACTIVATED);
        }
        this.userStatus = UserStatus.DEACTIVATED.name();
    }

    /**
     * 계정 활성화 (Admin 전용)
     */
    public void activate() {
        this.userStatus = UserStatus.ACTIVE.name();
    }

    /**
     * 계정 탈퇴 처리
     */
    public void withdraw() {
        this.userStatus = UserStatus.WITHDRAWN.name();
    }

    /**
     * 비활성화 여부 확인
     */
    public boolean isDeactivated() {
        return UserStatus.DEACTIVATED.name().equals(this.userStatus);
    }

    /**
     * 탈퇴 여부 확인
     */
    public boolean isWithdrawn() {
        return UserStatus.WITHDRAWN.name().equals(this.userStatus);
    }
}
