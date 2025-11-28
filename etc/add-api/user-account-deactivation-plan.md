# 사용자 계정 비활성화/탈퇴 기능 구현 목록

> 이 문서는 이미 존재하는 프로필 조회/수정 API를 기반으로, **추가로 구현이 필요해 보이는 사용자 계정 비활성화/탈퇴 기능**을 정리한 간단한 계획서입니다.
> 상세 설계는 추후 `etc/docs` 구현 계획서에 편입하는 것을 전제로 합니다.

---

## 1. 도메인/엔티티 수정

1. `EumeUser`의 `userStatus` 필드를 활용하여 다음 상태 전환을 명시
   - `ACTIVE` → `DEACTIVATED` (비활성화)
   - 필요 시, 향후 `DELETED` 등 추가 상태를 검토
2. 상태 전환을 위한 도메인 메서드 추가
   - `deactivate()` : 비활성화 처리 (로그인/서비스 이용 제한)
   - `activate()` : 필요 시 재활성화 처리
   - (선택) `markDeleted()` : 논리 삭제용 상태 전환

---

## 2. 서비스 계층

1. `EumeUserService` 또는 별도 `EumeUserAccountService`에 계정 상태 변경 메서드 추가
   - `deactivateUser(String email)`
   - (선택) `deleteUser(String email)`
2. 비활성화/삭제 시 처리 고려사항
   - 로그인/토큰 무효화 (기존 `logout` 로직 재사용 가능)
   - Admin/통계용 식별을 위해 최소한의 키 정보는 유지할지 여부 결정

---

## 3. Controller(API) 설계

1. 사용자 자기 계정 비활성화 요청 API
   - `POST /api/users/me/deactivate`
   - JWT 인증 필수 (`@AuthenticationPrincipal` 사용)
   - 응답: 단순 성공 메시지 + 변경된 상태(`userStatus`) 반환
2. (선택) 사용자 자기 계정 탈퇴 요청 API
   - `POST /api/users/me/withdraw`
   - 실제 물리 삭제 여부는 정책에 따라 결정 (즉시 삭제 vs. 일정 기간 후 배치 삭제)
3. Swagger 문서화
   - 기존 `EumeUserController`에 메서드 추가 + `@Operation`, `@ApiResponses` 작성

---

## 4. 보안/인증 및 소셜 연동 고려사항

1. `SecurityConfiguration`에서 `POST /api/users/me/deactivate`, `/withdraw` 경로에 인증 필요 설정
2. 비활성화된 사용자가 로그인 시도할 경우
   - 기존 `EumeUserErrorCode`와 일관된 에러 코드(`U_****`) 추가
   - "비활성화된 계정" 에러 메시지 반환
3. Google 소셜 로그인 연동 해제 처리
   - 비활성화/탈퇴 시, 저장된 Google 식별자(`providerId` 등)를 더 이상 로그인에 사용할 수 없도록 처리
   - (선택) Google OAuth 토큰이 저장되어 있다면, Google API를 통해 토큰 철회(revoke) 요청을 보내는 로직 검토
   - 이후 동일 Google 계정으로 재가입을 허용할지, 관리자 승인 후만 허용할지 정책 정의 필요

---

## 5. 후속 작업

1. 위 내용을 `etc/docs` 구현 계획서(v4 추가 기능 섹션 등)에 반영
2. Admin 사용자 관리 화면과 연동
   - Admin이 비활성화/재활성화할 수 있는 별도 API와 화면은 추후 Admin 기능 설계에서 추가
3. 테스트 코드 작성
   - 계정 비활성화 후 로그인/토큰 발급이 차단되는지 검증
   - 비활성화 상태에서의 기타 API 접근 제어 검증
