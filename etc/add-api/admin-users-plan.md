# Admin 이용자 관리 기능 구현 목록

> 이 문서는 `etc/docs/002, 003.implementation-plan.md` 에 정의된 Admin 사용자 목록/상세 조회 스펙을 기반으로,
> 실제 화면 요구사항(이용자 관리 시안)에 맞춰 **추가로 필요한 기능(상태 변경, Excel 내보내기 등)** 을 정리한 계획서입니다.

---

## 1. 목록 조회 vs. 상세 조회 전략

1. **목록 조회는 가볍게, 상세 정보는 별도 API에서 제공**하는 현재 설계를 유지합니다.
   - 목록 응답(`AdminUserResponse`)에는 테이블에 필요한 핵심 필드만 포함
     - id, email, userName, nickname, userStatus, sigunguName, lastLoginDate, createdAt 등
   - 상세 조회(`AdminUserDetailResponse`)에서만 민감 정보/추가 필드 제공
     - providerId, profileImage, birthDate, gender, phone, loginFailCount 등
2. 이유
   - 목록에서 모든 정보를 내려주면 응답이 커지고, 불필요하게 민감 정보가 노출될 수 있음
   - 이미 002/003 구현 계획서에 **목록 + 상세 조회가 분리된 구조**가 정의되어 있어, 이를 그대로 활용하는 것이 일관적

---

## 2. 이용자 목록 조회 (재확인)

- Endpoint: `GET /api/admin/users`
- Admin JWT 필요
- Query 파라미터
  - `page`, `size`, `status`, `keyword` (이미 문서에 정의된 대로 유지)
- Response
  - `AdminUserListResponse` + `AdminUserResponse` (002/003 문서 내용 재사용)

추가로, 화면 요구사항에 따라 다음을 고려할 수 있습니다.

- [ ] 정렬 옵션 추가 (최근 활동순, 이름순 등)
- [ ] 위험도 컬럼 필요 시, `AdminUserResponse` 에 위험도 점수/라벨 필드 추가 검토

---

## 3. 이용자 상세 조회 (재확인)

- Endpoint: `GET /api/admin/users/{userId}`
- Admin JWT 필요
- Response
  - `AdminUserDetailResponse` + `AdminSigunguResponse` (002/003 문서 내용 재사용)

목록에서 행 클릭 시 이 상세 API를 호출하여, 별도 상세 패널/페이지에서 전체 정보를 보여주는 패턴을 권장합니다.

---

## 4. 이용자 상태 변경 API 추가

1. **목표**
   - 관리자가 이용자 계정을 활성/비활성으로 전환할 수 있도록 합니다.
2. 엔드포인트 예시
   - `PATCH /api/admin/users/{userId}/status`
   - Request Body 예시:
     ```java
     public record AdminUserStatusUpdateRequest(
         @NotBlank String status   // ACTIVE 또는 DEACTIVATED
     ) {}
     ```
   - Response:
     - 성공 여부 + 변경된 상태를 담은 간단한 Response 또는 `AdminUserDetailResponse` 재사용
3. 구현 포인트
   - `EumeUser` 엔티티의 `userStatus` 필드를 `UserStatus` enum 과 일관되게 사용
   - 비활성화 시, 로그인/토큰 발급 로직에서 차단하도록 기존 User/Auth 로직과 연동
   - 에러 코드: 기존 `EumeUserErrorCode` 컨벤션(`U_****`)에 맞춰 추가

---

## 5. Excel 내보내기 API 추가

1. **목표**
   - 현재 필터 조건에 맞는 이용자 목록을 Excel/CSV 형태로 다운로드할 수 있게 합니다.
2. 엔드포인트 예시
   - `GET /api/admin/users/export`
   - Query 파라미터: 목록 조회와 동일한 `status`, `keyword` 등 재사용
   - 응답 방식 후보
     - (A) `Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` 로 Excel 바이너리 반환
     - (B) `text/csv` 또는 JSON 반환 후 프론트에서 파일로 저장
3. 구현 포인트
   - 대량 데이터 조회 시 페이징 없이 전체를 내려줄지, 최대 건수 제한을 둘지 정책 결정
   - 시간/범위 필터(예: 가입일 기간)를 추가로 둘지 여부 검토

---

## 6. 체크리스트

- [ ] 기존 002/003 구현 계획서의 **EumeUser 목록/상세 조회 스펙**을 기준으로 코드 구현 상태 확인
- [ ] `PATCH /api/admin/users/{userId}/status` API 설계/구현
- [ ] 상태 변경 로직과 User 로그인/인증 로직 연동
- [ ] `GET /api/admin/users/export` API 설계/구현
- [ ] 이용자 관리 화면과 상태 변경/내보내기 API 연동
