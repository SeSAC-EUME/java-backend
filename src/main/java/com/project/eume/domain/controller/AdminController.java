package com.project.eume.domain.controller;

import com.project.eume.domain.dto.request.AdminLoginRequest;
import com.project.eume.domain.dto.request.AdminRegisterRequest;
import com.project.eume.domain.dto.request.AdminUserStatusUpdateRequest;
import com.project.eume.domain.dto.response.*;
import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.entity.Sigungu;
import com.project.eume.domain.entity.UserEmotion;
import com.project.eume.domain.service.AdminExportService;
import com.project.eume.domain.service.AdminReportService;
import com.project.eume.domain.service.AdminSearchService;
import com.project.eume.domain.service.AdminService;
import com.project.eume.domain.service.SigunguSearchService;
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

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "관리자 전용 API")
public class AdminController {

    private final AdminService adminService;
    private final AdminSearchService adminSearchService;
    private final AdminExportService adminExportService;
    private final AdminReportService adminReportService;
    private final SigunguSearchService sigunguSearchService;

    @GetMapping("/orgs")
    @Operation(summary = "소속 기관 목록 조회", description = "관리자 로그인 화면에서 사용할 기관(시군구) 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    public ResponseEntity<List<AdminOrgResponse>> getOrgs() {
        List<Sigungu> sigungus = sigunguSearchService.findAll();
        List<AdminOrgResponse> responses = sigungus.stream()
                .map(AdminOrgResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/register")
    @Operation(summary = "관리자 회원가입", description = "새 관리자 계정을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (필수 필드 누락)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 기관"),
            @ApiResponse(responseCode = "409", description = "중복된 로그인 ID 또는 이메일")
    })
    public ResponseEntity<AdminRegisterResponse> register(
            @Valid @RequestBody AdminRegisterRequest request
    ) {
        AdminRegisterResponse response = adminService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "관리자 로그인", description = "소속 기관, 관리자 ID/PW로 로그인합니다. 성공 시 JWT 토큰이 쿠키에 저장됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "소속 기관 불일치"),
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

    @GetMapping("/users/export")
    @Operation(summary = "이용자 데이터 Excel 내보내기", description = "필터 조건에 맞는 이용자 데이터를 Excel 파일로 다운로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "파일 생성 실패")
    })
    public ResponseEntity<Resource> exportUsersToExcel(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword
    ) {
        byte[] excelData = adminExportService.exportUsersToExcel(status, keyword);
        String filename = "users_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(new ByteArrayResource(excelData));
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


    @PatchMapping("/users/{userId}/status")
    @Operation(summary = "이용자 상태 변경", description = "특정 사용자의 상태를 활성/비활성으로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 상태 값"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<AdminUserStatusUpdateResponse> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserStatusUpdateRequest request
    ) {
        AdminUserStatusUpdateResponse response = adminService.updateUserStatus(userId, request.status());
        return ResponseEntity.ok(response);
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

    @GetMapping("/reports/summary")
    @Operation(summary = "보고서 요약 조회", description = "기간별 이용자 활동, AI 대화, 감정 분석 등 요약 데이터를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 날짜 범위"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<AdminReportSummaryResponse> getReportSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        AdminReportSummaryResponse response = adminReportService.getReportSummary(fromDate, toDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports/export")
    @Operation(summary = "보고서 Excel 다운로드", description = "보고서 데이터를 Excel 파일로 다운로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 날짜 범위"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "파일 생성 실패")
    })
    public ResponseEntity<Resource> exportReportToExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        byte[] excelData = adminReportService.exportReportToExcel(fromDate, toDate);
        String filename = "report_" + fromDate + "_" + toDate + ".xlsx";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(new ByteArrayResource(excelData));
    }
}
