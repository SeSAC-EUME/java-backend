package com.project.eume.domain.controller;

import com.project.eume.domain.dto.request.UserChatContentCreateRequest;
import com.project.eume.domain.dto.request.UserChatListCreateRequest;
import com.project.eume.domain.dto.response.UserChatContentCreateResponse;
import com.project.eume.domain.dto.response.UserChatContentListResponse;
import com.project.eume.domain.dto.response.UserChatListCreateResponse;
import com.project.eume.domain.dto.response.UserChatListListResponse;
import com.project.eume.domain.entity.UserChatContent;
import com.project.eume.domain.entity.UserChatList;
import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.service.UserChatRegisterService;
import com.project.eume.domain.service.UserChatSearchService;
import com.project.eume.domain.service.UserChatService;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-chats")
@Tag(name = "UserChat", description = "AI 다중 채팅 관련 API")
public class UserChatController {

    private final EumeUserSearchService eumeUserSearchService;
    private final UserChatSearchService userChatSearchService;
    private final UserChatRegisterService userChatRegisterService;
    private final UserChatService userChatService;

    @GetMapping
    @Operation(summary = "채팅방 목록 조회", description = "로그인한 사용자의 채팅방 목록을 페이지네이션하여 조회합니다. 최근 업데이트순으로 정렬됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 없음 또는 유효하지 않음)")
    })
    public ResponseEntity<UserChatListListResponse> getChatRooms(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        String email = userDetails.getUsername();
        EumeUser user = eumeUserSearchService.findByEmail(email);
        Page<UserChatList> chatRooms = userChatSearchService.findByEumeUserId(user.getId(), page, size);
        return ResponseEntity.ok(UserChatListListResponse.from(chatRooms));
    }

    @PostMapping
    @Operation(summary = "채팅방 생성", description = "AI와의 새로운 다중 채팅방을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "채팅방 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (제목 없음 또는 100자 초과)"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 없음 또는 유효하지 않음)")
    })
    public ResponseEntity<UserChatListCreateResponse> createChatRoom(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserChatListCreateRequest request
    ) {
        String email = userDetails.getUsername();
        EumeUser user = eumeUserSearchService.findByEmail(email);
        UserChatList chatRoom = userChatRegisterService.createChatRoom(user, request.roomTitle());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserChatListCreateResponse.from(chatRoom));
    }

    @GetMapping("/{chatListId}/contents")
    @Operation(summary = "메시지 목록 조회", description = "채팅방의 메시지 목록을 페이지네이션하여 조회합니다. 최신순으로 정렬됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 없음 또는 유효하지 않음)"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음")
    })
    public ResponseEntity<UserChatContentListResponse> getContents(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatListId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        String email = userDetails.getUsername();
        Page<UserChatContent> contents = userChatService.getContents(email, chatListId, page, size);
        return ResponseEntity.ok(UserChatContentListResponse.from(contents));
    }

    @PostMapping("/{chatListId}/contents")
    @Operation(summary = "메시지 발송", description = "채팅방에 메시지를 발송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "메시지 발송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (메시지 내용 또는 타입 없음)"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 없음 또는 유효하지 않음)"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음")
    })
    public ResponseEntity<UserChatContentCreateResponse> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long chatListId,
            @Valid @RequestBody UserChatContentCreateRequest request
    ) {
        String email = userDetails.getUsername();
        UserChatContentCreateResponse response = userChatService.sendMessage(email, chatListId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
