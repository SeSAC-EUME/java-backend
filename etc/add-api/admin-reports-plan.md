# Admin 보고서(리포트) 기능 구현 목록

> 이 문서는 시안의 "보고서" 화면을 기준으로,
> **어떤 데이터가 필요하고, 어떤 API/파일 다운로드 기능이 필요한지**를 정리한 계획서입니다.
> 실제 통계/집계 방식은 추후 구현 단계에서 구체화합니다.

---

## 1. 보고서 화면에 필요한 데이터 묶음

보고서 화면은 기간(예: 이번 달, 특정 날짜 범위)을 기준으로 다음 데이터들을 한 번에 보여줍니다.

1. **이용자 활동 요약**
   - 총 이용자 수
   - 활성 이용자 수
   - 신규 가입자 수
   - 평균 활동 시간
   - 일별 활성 이용자 수 그래프

2. **AI 대화 요약**
   - 총 대화 수
   - 일평균 대화 수
   - 상위 대화 많은 이용자 TOP N

3. **감정 분석 요약**
   - 정서적 주의가 필요한 이용자 수
   - 감정 점수 분포 (매우 좋음 ~ 매우 우울)

4. **긴급 상황 요약**
   - 총 긴급 요청 수
   - 평균 응답 시간
   - 상태별 건수(대기 중 / 처리 완료 등)

5. **일별 상세 테이블**
   - 날짜별:
     - 활성 이용자 수
     - 대화 수
     - 긴급 요청 수
     - 평균 감정 점수 등

---

## 2. 보고서 요약 데이터 조회 API

1. 엔드포인트 예시
   - `GET /api/admin/reports/summary`
   - Query 파라미터:
     - `from` (LocalDate) - 시작일
     - `to` (LocalDate) - 종료일
     - (선택) `orgId` - 소속 기관 기준 필터
2. 응답 구조(개념)
   - `AdminReportSummaryResponse` (record 예시)
     - `UserActivitySummary userActivity`
     - `ConversationSummary conversation`
     - `EmotionSummary emotion`
     - `EmergencySummary emergency`
     - `List<DailyStats> dailyStats`
3. 구현 포인트
   - 이미 존재하는 도메인/Repository(EumeUser, UserEmotion, 대화 엔티티, 긴급 엔티티 등)를 재사용하여 집계
   - 기간/기관 필터를 공통으로 적용할 수 있는 서비스 계층 설계

---

## 3. 파일 다운로드 API (PDF / Excel)

1. 엔드포인트 예시
   - `GET /api/admin/reports/export`
   - Query 파라미터:
     - `from`, `to`, `orgId` (summary와 동일)
     - `format` = `pdf` 또는 `excel`
2. 동작 개요
   - 내부에서 `AdminReportSummaryResponse` 를 생성하는 서비스 로직을 재사용
   - `format` 에 따라:
     - PDF: 템플릿 렌더링 후 `application/pdf` 로 바이너리 반환
     - Excel: 시트 생성 후 `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` 로 바이너리 반환
3. 구현 포인트
   - 요청 처리 시간이 길어질 경우, 추후 비동기 처리(보고서 생성 작업 + 상태 조회 API)로 확장 가능
   - 파일 이름 규칙 예시: `eume-report-YYYYMMDD-YYYYMMDD.xlsx` / `.pdf`

---

## 4. 재사용/의존 관계

1. **이용자 관리 기능과의 연계**
   - 보고서에서 사용하는 이용자 수/활성 수/신규 가입 수는
     - `EumeUserRepository` 의 집계 쿼리로 계산
2. **감정/대화/긴급 기능과의 연계**
   - UserEmotion, 대화 로그, 긴급 요청 엔티티에서 기간별 count/average 를 집계
3. **기관(orgId) 필터**
   - Admin 로그인 시 선택한 기관 정보와 연결되는 경우,
   - `orgId` 기준으로 사용자/대화/감정/긴급 데이터에 공통 필터 적용

---

## 5. 체크리스트

- [ ] `AdminReportSummaryResponse` 및 서브 DTO(UserActivitySummary 등) 설계
- [ ] `GET /api/admin/reports/summary` API 설계/구현
- [ ] `GET /api/admin/reports/export` API 설계/구현 (PDF/Excel 포맷 분기 포함)
- [ ] 기존 이용자/대화/감정/긴급 관련 Repository 에 집계용 메서드 추가
- [ ] 보고서 화면과 summary / export API 연동
