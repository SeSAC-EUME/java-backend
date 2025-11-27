package com.project.eume.domain.controller;

import com.project.eume.domain.dto.request.AdminLoginRequest;
import com.project.eume.domain.dto.response.*;
import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.entity.UserEmotion;
import com.project.eume.domain.service.AdminSearchService;
import com.project.eume.domain.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "관리자 전용 API")
public class AdminController {

    private final AdminService adminService;
    private final AdminSearchService adminSearchService;

    @PostMapping("/login")
    @Operation(summary = "관리자 로그인", description = "관리자 ID/PW로 로그인합니다. 성공 시 JWT 토큰이 쿠키에 저장됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (ID 없음 또는 비밀번호 불일치)"),
            @ApiResponse(responseCode = "403", description = "비활성화된 계정"),
            @ApiResponse(responseCode = "423", description = "계정 잠김 (로그인 실패 5회 초과)")
    })
    public ResponseEntity<AdminLoginResponse> login(
            @Valid @RequestBody AdminLoginRequest request,
            HttpServletResponse response
    ) {
        AdminLoginResponse loginResponse = adminService.login(request, response);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "관리자 로그아웃", description = "JWT 토큰 쿠키를 제거하여 로그아웃 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 없음 또는 유효하지 않음)")
    })
    public ResponseEntity<AdminLogoutResponse> logout(HttpServletResponse response) {
        adminService.logout(response);
        return ResponseEntity.ok(AdminLogoutResponse.success());
    }

    @GetMapping("/users")
    @Operation(summary = "사용자 목록 조회", description = "전체 사용자 목록을 페이지네이션하여 조회합니다. 상태 필터 및 검색어로 필터링할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 없음 또는 유효하지 않음)")
    })
    public ResponseEntity<AdminUserListResponse> getUsers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword
    ) {
        Page<EumeUser> users = adminSearchService.findUsers(page, size, status, keyword);
        return ResponseEntity.ok(AdminUserListResponse.from(users));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "사용자 상세 조회", description = "특정 사용자의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 없음 또는 유효하지 않음)"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<AdminUserDetailResponse> getUserDetail(@PathVariable Long userId) {
        EumeUser user = adminSearchService.findUserById(userId);
        return ResponseEntity.ok(AdminUserDetailResponse.from(user));
    }

    @GetMapping("/users/{userId}/emotions")
    @Operation(summary = "사용자 감정 기록 조회", description = "특정 사용자의 감정 분석 기록을 페이지네이션하여 조회합니다. 날짜 범위로 필터링할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 없음 또는 유효하지 않음)"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<AdminUserEmotionListResponse> getUserEmotions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Page<UserEmotion> emotions = adminSearchService.findUserEmotions(userId, page, size, startDate, endDate);
        return ResponseEntity.ok(AdminUserEmotionListResponse.from(emotions));
    }
}
