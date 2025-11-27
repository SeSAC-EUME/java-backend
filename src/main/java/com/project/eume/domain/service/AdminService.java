package com.project.eume.domain.service;

import com.project.eume.config.security.jwt.JwtUtil;
import com.project.eume.domain.dto.request.AdminLoginRequest;
import com.project.eume.domain.dto.response.AdminLoginResponse;
import com.project.eume.domain.entity.EumeAdmin;
import com.project.eume.domain.enums.JwtRule;
import com.project.eume.domain.enums.JwtType;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final int ACCESS_TOKEN_MAX_AGE = 60 * 60; // 1시간
    private static final int REFRESH_TOKEN_MAX_AGE = 60 * 60 * 24 * 30; // 30일

    @Transactional
    public AdminLoginResponse login(AdminLoginRequest request, HttpServletResponse response) {
        // 1. 관리자 조회
        EumeAdmin admin = adminSearchService.findByAdminLoginId(request.adminLoginId());

        // 2. 계정 잠금 확인
        if (admin.isLocked()) {
            throw new AdminException(AdminErrorCode.ACCOUNT_LOCKED);
        }

        // 3. 계정 활성화 확인
        if (!admin.isActive()) {
            throw new AdminException(AdminErrorCode.DEACTIVATED_ADMIN);
        }

        // 4. 비밀번호 확인
        if (!passwordEncoder.matches(request.adminPw(), admin.getAdminPw())) {
            admin.loginFail();
            throw new AdminException(AdminErrorCode.INVALID_PASSWORD);
        }

        // 5. 로그인 성공 처리
        admin.loginSuccess();

        // 6. JWT 토큰 생성 및 쿠키 설정
        String code = UUID.randomUUID().toString();
        String accessToken = jwtUtil.generateJwt(admin.getAdminEmail(), code, JwtType.ACCESS_TOKEN);
        String refreshToken = jwtUtil.generateJwt(admin.getAdminEmail(), code, JwtType.REFRESH_TOKEN);

        addTokenCookie(response, JwtRule.ACCESS_PREFIX.getValue(), accessToken, ACCESS_TOKEN_MAX_AGE);
        addTokenCookie(response, JwtRule.REFRESH_PREFIX.getValue(), refreshToken, REFRESH_TOKEN_MAX_AGE);

        return AdminLoginResponse.from(admin);
    }

    public void logout(HttpServletResponse response) {
        // 쿠키 삭제
        deleteCookie(response, JwtRule.ACCESS_PREFIX.getValue());
        deleteCookie(response, JwtRule.REFRESH_PREFIX.getValue());
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
