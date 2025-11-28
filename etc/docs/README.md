# EUME 프로젝트 문서 목록

> 이 폴더는 프로젝트 구현 계획 및 문서를 관리합니다.
> 파일명 컨벤션: `{번호}.{제목}.md`

---

## 📚 문서 목록

### 001. API 문서
**파일:** `001.api-docs.md`
- API 엔드포인트 명세서
- 요청/응답 예시

---

### 002. 구현 계획

#### 002-1. 구현 계획서 (메인)
**파일:** `002.implementation-plan.md`
- User - EumeChat 기능
- User - UserChat 기능
- Admin 기능
- 파일 생성 요약 및 구현 순서

#### 002-2. Codex 리뷰
**파일:** `002.codex-review.md`
- Codex에서 생성한 리뷰 문서

---

### 003. 구현 계획 v3

#### 003-1. 구현 계획서 v3
**파일:** `003.implementation-plan.md`
- **v3 변경사항:** Swagger 문서화 추가
- @Operation, @ApiResponses 어노테이션 추가
- Controller 전체 코드 예시

#### 003-2. 검토 리포트
**파일:** `003.review-report.md`
- 003 파일의 프로젝트 컨벤션 검토 결과
- 10가지 이슈 분석 및 수정 방안

---

### 004. 구현 계획 v4 (최신) ⭐

#### 004-1. 구현 계획서 v4
**파일:** `004.implementation-plan.md`
- **v4 변경사항 (2025-11-27):**
  - DTO 패키지 구조 명시 (request/, response/)
  - EumeChatRegisterService 불필요 의존성 제거
  - Response 정적 팩토리 메서드 success()로 통일
  - updateLastChatTime() 메서드 제거 (JPA 자동 처리)
  - PasswordEncoder, WebClient Bean 설정 명시

#### 004-2. 변경사항 요약
**파일:** `004.changes-summary.md`
- 003 → 004 변경사항 상세 비교
- 5가지 주요 수정사항
- 프로젝트 컨벤션 준수 확인

---

## 📋 파일명 컨벤션

### 규칙
```
{번호}.{제목}.md
```

### 예시
- ✅ `001.api-docs.md`
- ✅ `002.implementation-plan.md`
- ✅ `003.review-report.md`
- ✅ `004.changes-summary.md`
- ❌ `003-review-report.md` (하이픈 사용 금지)
- ❌ `004_changes_summary.md` (언더스코어 사용 금지)

---

## 🚀 최신 문서

**현재 사용할 문서:**
- **구현 계획:** `004.implementation-plan.md` (v4 - 최신)
- **변경사항:** `004.changes-summary.md`

**이전 버전 (참고용):**
- v3: `003.implementation-plan.md`
- v2: `002.implementation-plan.md`

---

## 📝 문서 작성 가이드

### 새 문서 추가 시
1. 가장 큰 번호 + 1로 시작 (예: 다음은 `005`)
2. 제목은 영문 소문자, 하이픈(-)으로 단어 구분
3. 파일 인코딩: **UTF-8**
4. 줄바꿈: **CRLF (Windows)**

### 예시
```
005.database-schema.md
006.deployment-guide.md
007.troubleshooting.md
```

---

## 🗂️ 파일 목록 (정렬됨)

```
etc/docs/
├── README.md                        (본 문서)
├── 001.api-docs.md                  (API 명세)
├── 002.codex-review.md              (Codex 리뷰)
├── 002.implementation-plan.md       (구현 계획 v2)
├── 003.implementation-plan.md       (구현 계획 v3)
├── 003.review-report.md             (v3 검토 리포트)
├── 004.changes-summary.md           (v4 변경사항)
└── 004.implementation-plan.md       (구현 계획 v4) ⭐
```

---

## 📌 참고

- 프로젝트 환경: Windows 11, UTF-8, CRLF
- 문서 언어: 한글/영문 혼용
- 마크다운 에디터: VS Code 권장
