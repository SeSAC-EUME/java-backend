package com.project.eume.domain.service;

import com.project.eume.domain.dto.request.EumeUserUpdateRequest;
import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.entity.Sigungu;
import com.project.eume.domain.enums.JwtRule;
import com.project.eume.domain.repository.EumeUserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class EumeUserService {

    private final EumeUserSearchService userSearchService;
    private final EumeUserRepository userRepository;
    private final SigunguSearchService sigunguSearchService;

    @Transactional
    public void logout(HttpServletResponse response) {
        // JWT 쿠키 제거 (maxAge를 0으로 설정하여 즉시 만료)
        removeCookie(response, JwtRule.ACCESS_PREFIX.getValue());
        removeCookie(response, JwtRule.REFRESH_PREFIX.getValue());

        // JSESSIONID 쿠키도 제거 (Spring Security 세션 쿠키)
        removeCookie(response, "JSESSIONID");

        log.info("User logout successful");
    }

    @Transactional
    public EumeUser updateUser(String email, EumeUserUpdateRequest request) {
        // 이메일로 사용자 찾기
        EumeUser user = userSearchService.findByEmail(email);
        Sigungu newSigungu = sigunguSearchService.findByIdOrNull(request.sigunguId());
        user.update(request, newSigungu);

        // 변경사항 저장
        EumeUser updatedUser = userRepository.save(user);

        log.info("User profile updated successfully: email={}", email);

        return updatedUser;
    }


    /**
     * 사용자 계정 비활성화
     *
     * @param email    사용자 이메일
     * @param response HTTP 응답 (쿠키 삭제용)
     * @return 비활성화된 사용자
     */
    @Transactional
    public EumeUser deactivateUser(String email, HttpServletResponse response) {
        EumeUser user = userSearchService.findByEmail(email);
        user.deactivate();

        // JWT 쿠키 삭제 (로그아웃 처리)
        removeCookie(response, JwtRule.ACCESS_PREFIX.getValue());
        removeCookie(response, JwtRule.REFRESH_PREFIX.getValue());
        removeCookie(response, "JSESSIONID");

        log.info("User account deactivated: email={}", email);
        return user;
    }

    /**
     * 사용자 계정 탈퇴
     *
     * @param email    사용자 이메일
     * @param response HTTP 응답 (쿠키 삭제용)
     * @return 탈퇴 처리된 사용자
     */
    @Transactional
    public EumeUser withdrawUser(String email, HttpServletResponse response) {
        EumeUser user = userSearchService.findByEmail(email);
        user.withdraw();

        // JWT 쿠키 삭제 (로그아웃 처리)
        removeCookie(response, JwtRule.ACCESS_PREFIX.getValue());
        removeCookie(response, JwtRule.REFRESH_PREFIX.getValue());
        removeCookie(response, "JSESSIONID");

        log.info("User account withdrawn: email={}", email);
        return user;
    }

    private void removeCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }
}
