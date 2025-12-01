package com.project.eume.domain.controller;

import com.project.eume.domain.dto.request.EumeChatContentCreateRequest;
import com.project.eume.domain.dto.response.EumeChatContentCreateResponse;
import com.project.eume.domain.dto.response.EumeChatContentListResponse;
import com.project.eume.domain.dto.response.EumeChatListCreateResponse;
import com.project.eume.domain.dto.response.EumeChatListDetailResponse;
import com.project.eume.domain.entity.EumeChatContent;
import com.project.eume.domain.entity.EumeChatList;
import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.service.EumeChatRegisterService;
import com.project.eume.domain.service.EumeChatSearchService;
import com.project.eume.domain.service.EumeChatService;
import com.project.eume.domain.service.EumeUserSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/eume-chats")
@Tag(name = "EumeChat", description = "Eume AI 채팅 관련 API")
public class EumeChatController {

    private final EumeUserSearchService eumeUserSearchService;
    private final EumeChatSearchService eumeChatSearchService;
    private final EumeChatRegisterService eumeChatRegisterService;
    private final EumeChatService eumeChatService;

    @GetMapping("/me")
    @Operation(summary = "내 채팅방 조회", description = "로그인한 사용자의 Eume 채팅방 존재 여부 및 상세 정보를 조회합니다. 채팅방이 없으면 null을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 없음 또는 유효하지 않음)")
    })
    public ResponseEntity<EumeChatListDetailResponse> getMyChatList(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        EumeUser user = eumeUserSearchService.findByEmail(email);
        Optional<EumeChatList> chatList = eumeChatSearchService.findByEumeUserIdOrOptional(user.getId());
        return ResponseEntity.ok(EumeChatListDetailResponse.from(chatList.orElse(null)));
    }

    @PostMapping
    @Operation(summary = "채팅방 생성", description = "Eume AI와의 채팅방을 생성합니다. 사용자당 1개만 생성 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "채팅방 생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 없음 또는 유효하지 않음)"),
            @ApiResponse(responseCode = "409", description = "이미 채팅방이 존재함")
    })
    public ResponseEntity<EumeChatListCreateResponse> createChatList(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        EumeUser user = eumeUserSearchService.findByEmail(email);
        EumeChatList chatList = eumeChatRegisterService.createChatList(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EumeChatListCreateResponse.from(chatList));
    }

    @GetMapping("/{chatListId}/contents")
    @Operation(summary = "대화 내용 목록 조회", description = "Eume 채팅방의 대화 내용을 페이지네이션하여 조회합니다. 최신순으로 정렬됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 없음 또는 유효하지 않음)"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음 (본인의 채팅방이 아님)"),
            @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음")
    })
    public ResponseEntity<EumeChatContentListResponse> getContents(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatListId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        String email = userDetails.getUsername();
        EumeUser user = eumeUserSearchService.findByEmail(email);

        // 채팅 목록 조회 및 권한 검증
        EumeChatList chatList = eumeChatSearchService.findById(chatListId);
        eumeChatService.validateChatListOwnership(chatList, user);

        Page<EumeChatContent> contents = eumeChatSearchService
                .findContentsByEumeChatListId(chatListId, page, size);
        return ResponseEntity.ok(EumeChatContentListResponse.from(contents));
    }

    @PostMapping("/{chatListId}/contents")
    @Operation(summary = "메시지 발송", description = "Eume AI에게 메시지를 발송하고 AI 응답을 받습니다. 사용자 메시지와 AI 응답이 함께 저장됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "메시지 발송 및 AI 응답 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (메시지 내용 없음)"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 없음 또는 유효하지 않음)"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음 (본인의 채팅방이 아님)"),
            @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "AI 응답 생성 실패")
    })
    public Mono<ResponseEntity<EumeChatContentCreateResponse>> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatListId,
            @Valid @RequestBody EumeChatContentCreateRequest request
    ) {
        String email = userDetails.getUsername();
        return eumeChatService.sendMessage(email, chatListId, request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
}
