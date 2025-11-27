package com.project.eume.domain.controller;

import com.project.eume.domain.dto.request.EumeUserUpdateRequest;
import com.project.eume.domain.dto.response.EumeUserLogoutResponse;
import com.project.eume.domain.dto.response.EumeUserProfileResponse;
import com.project.eume.domain.dto.response.EumeUserUpdateResponse;
import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.service.EumeUserSearchService;
import com.project.eume.domain.service.EumeUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "사용자 관련 API")
public class EumeUserController {

    private final EumeUserSearchService eumeUserSearchService;
    private final EumeUserService eumeUserService;

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "JWT 토큰 쿠키를 제거하여 로그아웃 처리")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 없음 또는 유효하지 않음)")
    })
    public ResponseEntity<EumeUserLogoutResponse> logout(
        HttpServletResponse httpResponse
    ) {
        eumeUserService.logout(httpResponse);
        return ResponseEntity.ok(EumeUserLogoutResponse.successed());
    }

    @GetMapping("/me")
    @Operation(summary = "나의 정보 조회", description = "로그인한 사용자의 프로필 정보 조회 (JWT 토큰 필요)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 없음 또는 유효하지 않음)")
    })
    public ResponseEntity<EumeUserProfileResponse> getMyProfile(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        EumeUser user = eumeUserSearchService.findByEmail(email);
        EumeUserProfileResponse response = EumeUserProfileResponse.from(user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    @Operation(summary = "나의 정보 수정", description = "로그인한 사용자의 프로필 정보 수정 (displayName, avatarUrl만 수정 가능)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검증 실패)"),
        @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 없음 또는 유효하지 않음)")
    })
    public ResponseEntity<EumeUserUpdateResponse> updateMyProfile(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody EumeUserUpdateRequest request
    ) {
        String email = userDetails.getUsername();
        EumeUser updatedUser = eumeUserService.updateUser(email, request);
        EumeUserUpdateResponse response = EumeUserUpdateResponse.from(updatedUser);
        return ResponseEntity.ok(response);
    }
}
