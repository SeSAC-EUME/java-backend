package com.project.eume.domain.service;

import com.project.eume.config.security.jwt.JwtUtil;
import com.project.eume.domain.dto.request.AdminLoginRequest;
import com.project.eume.domain.dto.request.AdminRegisterRequest;
import com.project.eume.domain.dto.response.AdminLoginResponse;
import com.project.eume.domain.dto.response.AdminRegisterResponse;
import com.project.eume.domain.dto.response.AdminUserStatusUpdateResponse;
import com.project.eume.domain.entity.EumeAdmin;
import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.entity.Sigungu;
import com.project.eume.domain.enums.JwtRule;
import com.project.eume.domain.enums.JwtType;
import com.project.eume.domain.repository.EumeAdminRepository;
import com.project.eume.exceptions.errorcode.AdminErrorCode;
import com.project.eume.exceptions.exception.AdminException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AdminService {
    private final AdminSearchService adminSearchService;
    private final SigunguSearchService sigunguSearchService;
    private final EumeAdminRepository eumeAdminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final int ACCESS_TOKEN_MAX_AGE = 60 * 60 * 24; // 24시간

    @Transactional
    public AdminLoginResponse login(AdminLoginRequest request, HttpServletResponse response) {
        // 1. 관리자 조회
        EumeAdmin admin = adminSearchService.findByAdminLoginId(request.adminLoginId());

        // 2. 소속 기관 확인
        if (admin.getSigungu() == null || !admin.getSigungu().getId().equals(request.sigunguId())) {
            throw new AdminException(AdminErrorCode.INVALID_SIGUNGU);
        }

        // 3. 계정 잠금 확인
        if (admin.isLocked()) {
            throw new AdminException(AdminErrorCode.ACCOUNT_LOCKED);
        }

        // 4. 계정 활성화 확인
        if (!admin.isActive()) {
            throw new AdminException(AdminErrorCode.DEACTIVATED_ADMIN);
        }

        // 5. 비밀번호 확인
        if (!passwordEncoder.matches(request.adminPw(), admin.getAdminPw())) {
            admin.loginFail();
            throw new AdminException(AdminErrorCode.INVALID_PASSWORD);
        }

        // 6. 로그인 성공 처리
        admin.loginSuccess();

        // 7. JWT 토큰 생성 및 쿠키 설정
        String code = UUID.randomUUID().toString();
        String accessToken = jwtUtil.generateJwt(admin.getAdminLoginId(), code, JwtType.ACCESS_TOKEN);

        addTokenCookie(response, JwtRule.ADMIN_ACCESS_PREFIX.getValue(), accessToken, ACCESS_TOKEN_MAX_AGE);

        return AdminLoginResponse.from(admin);
    }

    @Transactional
    public AdminRegisterResponse register(AdminRegisterRequest request) {
        // 1. 로그인 ID 중복 확인
        if (eumeAdminRepository.existsByAdminLoginId(request.adminLoginId())) {
            throw new AdminException(AdminErrorCode.DUPLICATE_LOGIN_ID);
        }

        // 2. 이메일 중복 확인
        if (eumeAdminRepository.existsByAdminEmail(request.adminEmail())) {
            throw new AdminException(AdminErrorCode.DUPLICATE_EMAIL);
        }

        // 3. 소속 기관 조회
        Sigungu sigungu = sigunguSearchService.findByIdOrNull(request.sigunguId());
        if (sigungu == null) {
            throw new AdminException(AdminErrorCode.SIGUNGU_NOT_FOUND);
        }

        // 4. 관리자 생성
        EumeAdmin admin = EumeAdmin.builder()
                .adminLoginId(request.adminLoginId())
                .adminPw(passwordEncoder.encode(request.adminPw()))
                .adminName(request.adminName())
                .adminEmail(request.adminEmail())
                .adminPhone(request.adminPhone())
                .sigungu(sigungu)
                .adminStatus("ACTIVE")
                .loginFailCount(0)
                .build();

        EumeAdmin savedAdmin = eumeAdminRepository.save(admin);
        return AdminRegisterResponse.from(savedAdmin);
    }

    public void logout(HttpServletResponse response) {
        // 쿠키 삭제
        deleteCookie(response, JwtRule.ADMIN_ACCESS_PREFIX.getValue());
    }


    /**
     * 이용자 상태 변경 (관리자 전용)
     *
     * @param userId    대상 사용자 ID
     * @param newStatus 변경할 상태 (ACTIVE/DEACTIVATED)
     * @return 변경 결과
     */
    @Transactional
    public AdminUserStatusUpdateResponse updateUserStatus(Long userId, String newStatus) {
        EumeUser user = adminSearchService.findUserById(userId);
        String previousStatus = user.getUserStatus();

        if ("ACTIVE".equals(newStatus)) {
            user.activate();
        } else if ("DEACTIVATED".equals(newStatus)) {
            user.deactivate();
        } else {
            throw new AdminException(AdminErrorCode.INVALID_STATUS);
        }

        return AdminUserStatusUpdateResponse.of(userId, previousStatus, newStatus);
    }

    private void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    private void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
