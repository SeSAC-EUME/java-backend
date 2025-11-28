# 관리자 로그인 및 기관 목록(API) 구현 목록

> 이 문서는 기존 Admin 로그인 구현 계획에 더해,
> **소속 기관 드롭다운 + Sigungu 기반 기관 목록 조회 API** 를 어떻게 구현할지 간단히 정리한 계획서입니다.

---

## 1. 엔드포인트 설계

### 1.1 관리자 로그인 API (요약 확인)

- `POST /api/admin/login`
  - Request: `AdminLoginRequest(adminLoginId, adminPw)`
  - Response: `AdminLoginResponse(id, adminName, adminEmail, lastLoginDate)`
  - 동작 개요:
    - 관리자 조회 → 계정 상태/잠금 여부 확인 → 비밀번호 검증 → 로그인 성공/실패 처리 → Admin JWT 발급 및 쿠키 저장

※ 이 부분은 기존 `etc/docs` 구현 계획서 내용 재확인 용도로만 기록합니다.

### 1.2 소속 기관 목록 조회 API

- `GET /api/admin/orgs`
  - 설명: 관리자 로그인 화면의 "소속 기관" 드롭다운에 사용할 목록 조회
  - 인증: 없음 (로그인 화면에서 호출)
  - 응답 DTO 예시:
    ```java
    public record AdminOrgResponse(
        Long id,        // Sigungu id 또는 추후 Organization id
        String name      // 화면에 표시할 이름 (예: "서울시 은평구 신사동")
    ) {}
    ```
  - 응답 형식 예시:
    - JSON 배열: `[ { "id": 1, "name": "서울시 은평구 신사동" }, ... ]`

---

## 2. Sigungu 기반 구현 전략

1. **기본 데이터 소스**
   - `Sigungu` 엔티티(`id`, `sido`, `sigungu`)와 `SigunguRepository` 를 그대로 활용
2. **조회 서비스 추가/확장**
   - 기존 `SigunguSearchService` 에 다음 메서드 추가 검토
     - `List<Sigungu> findAll()`
   - 또는 별도 `AdminOrgSearchService` 를 두고 내부에서 `SigunguRepository.findAll()` 호출
3. **표시 이름 구성 규칙**
   - 기본 규칙 예시: `sido + " " + sigungu` → "서울시 은평구" 형태
   - 필요 시 동 이름까지 포함하는 등 UI 요구사항에 따라 조정

---

## 3. Controller 추가

1. `AdminController` (또는 별도 `AdminOrgController`) 에 메서드 추가
   - `@GetMapping("/orgs")`
   - `@Operation(summary = "소속 기관 목록 조회", description = "관리자 로그인 화면에서 사용할 기관(시군구) 목록을 조회합니다.")`
   - Swagger `@ApiResponses` 로 200/500 정도만 명시
2. 응답 변환
   - `List<Sigungu>` → `List<AdminOrgResponse>` 매핑 후 반환

---

## 4. 향후 확장 포인트

1. **별도 Organization 엔티티 도입 가능성**
   - 실제 기관(복지관, 센터 등)이 Sigungu 보다 더 세분화될 경우
   - `Organization(id, name, sigunguId, type, address, ...)` 엔티티 추가 검토
2. **권한/역할 연동**
   - 추후 Admin 계정을 특정 기관에 귀속시키거나,
   - 여러 기관을 관리하는 상위 관리자 개념을 도입할 때
   - `EumeAdmin` ↔ `Organization` (N:1 또는 N:N) 관계 설계 필요
3. **캐싱/성능**
   - 기관 목록은 자주 변하지 않으므로, Spring Cache 등을 이용한 캐싱 고려

---

## 5. 체크리스트

- [ ] `AdminOrgResponse` DTO 생성
- [ ] Sigungu 조회용 서비스 메서드 추가 (`findAll` 등)
- [ ] `GET /api/admin/orgs` 컨트롤러 메서드 추가 및 Swagger 문서화
- [ ] 프론트엔드 로그인 화면에서 `/api/admin/orgs` 를 호출하도록 연동
- [ ] 추후 Organization 엔티티 도입 여부/시점 결정
