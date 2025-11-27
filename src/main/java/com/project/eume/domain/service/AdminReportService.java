package com.project.eume.domain.service;

import com.project.eume.domain.dto.response.*;
import com.project.eume.domain.repository.EumeChatContentRepository;
import com.project.eume.domain.repository.EumeChatListRepository;
import com.project.eume.domain.repository.EumeUserRepository;
import com.project.eume.exceptions.errorcode.AdminErrorCode;
import com.project.eume.exceptions.exception.AdminException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AdminReportService {

    private final EumeUserRepository eumeUserRepository;
    private final EumeChatListRepository eumeChatListRepository;
    private final EumeChatContentRepository eumeChatContentRepository;

    /**
     * 보고서 요약 데이터 조회
     *
     * @param fromDate 시작일
     * @param toDate   종료일
     * @return 보고서 요약 응답
     */
    public AdminReportSummaryResponse getReportSummary(LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            throw new AdminException(AdminErrorCode.INVALID_DATE_RANGE);
        }

        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.atTime(LocalTime.MAX);

        // 이용자 활동 요약
        UserActivitySummary userActivity = getUserActivitySummary(startDateTime, endDateTime);

        // 대화 요약
        ConversationSummary conversation = getConversationSummary(fromDate, toDate, startDateTime, endDateTime);

        // 감정 분석 요약 (현재는 기본값으로 반환)
        EmotionSummary emotion = getEmotionSummary();

        // 일별 통계
        List<DailyStatResponse> dailyStats = getDailyStats(fromDate, toDate);

        return AdminReportSummaryResponse.of(userActivity, conversation, emotion, dailyStats);
    }

    private UserActivitySummary getUserActivitySummary(LocalDateTime start, LocalDateTime end) {
        long totalUsers = eumeUserRepository.count();
        long activeUsers = eumeUserRepository.countByUserStatus("ACTIVE");
        long newUsers = eumeUserRepository.countByCreatedAtBetween(start, end);

        return new UserActivitySummary(totalUsers, activeUsers, newUsers);
    }

    private ConversationSummary getConversationSummary(LocalDate fromDate, LocalDate toDate,
                                                       LocalDateTime start, LocalDateTime end) {
        long totalConversations = eumeChatListRepository.countByCreatedAtBetween(start, end);
        long totalMessages = eumeChatContentRepository.countByCreatedAtBetween(start, end);

        long days = ChronoUnit.DAYS.between(fromDate, toDate) + 1;
        double dailyAvgConversations = days > 0 ? (double) totalConversations / days : 0.0;

        return new ConversationSummary(totalConversations, dailyAvgConversations, totalMessages);
    }

    private EmotionSummary getEmotionSummary() {
        // 감정 분석 기능은 추후 구현 시 확장
        Map<String, Long> emotionDistribution = new LinkedHashMap<>();
        emotionDistribution.put("매우좋음", 0L);
        emotionDistribution.put("좋음", 0L);
        emotionDistribution.put("보통", 0L);
        emotionDistribution.put("나쁨", 0L);
        emotionDistribution.put("매우나쁨", 0L);

        return new EmotionSummary(0L, 0.0, emotionDistribution);
    }

    private List<DailyStatResponse> getDailyStats(LocalDate fromDate, LocalDate toDate) {
        List<DailyStatResponse> dailyStats = new ArrayList<>();

        LocalDate current = fromDate;
        while (!current.isAfter(toDate)) {
            LocalDateTime dayStart = current.atStartOfDay();
            LocalDateTime dayEnd = current.atTime(LocalTime.MAX);

            long conversations = eumeChatListRepository.countByCreatedAtBetween(dayStart, dayEnd);

            dailyStats.add(new DailyStatResponse(
                current,
                0L, // activeUsers - 추후 lastLoginDate 기반 구현
                conversations,
                0.0 // avgEmotionScore - 추후 구현
            ));

            current = current.plusDays(1);
        }

        return dailyStats;
    }

    /**
     * 보고서 Excel 파일 생성
     *
     * @param fromDate 시작일
     * @param toDate   종료일
     * @return Excel 파일 바이트 배열
     */
    public byte[] exportReportToExcel(LocalDate fromDate, LocalDate toDate) {
        AdminReportSummaryResponse summary = getReportSummary(fromDate, toDate);

        try (Workbook workbook = new XSSFWorkbook()) {
            // Sheet 1: 요약
            createSummarySheet(workbook, summary, fromDate, toDate);

            // Sheet 2: 일별 통계
            createDailyStatsSheet(workbook, summary.dailyStats());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Report export failed", e);
            throw new AdminException(AdminErrorCode.EXPORT_FAILED);
        }
    }

    private void createSummarySheet(Workbook workbook, AdminReportSummaryResponse summary,
                                    LocalDate fromDate, LocalDate toDate) {
        Sheet sheet = workbook.createSheet("요약");

        CellStyle headerStyle = createHeaderStyle(workbook);
        int rowNum = 0;

        // 기간 정보
        Row periodRow = sheet.createRow(rowNum++);
        periodRow.createCell(0).setCellValue("보고서 기간");
        periodRow.createCell(1).setCellValue(fromDate + " ~ " + toDate);

        rowNum++; // 빈 줄

        // 이용자 활동 요약
        Row userHeader = sheet.createRow(rowNum++);
        Cell userHeaderCell = userHeader.createCell(0);
        userHeaderCell.setCellValue("이용자 활동 요약");
        userHeaderCell.setCellStyle(headerStyle);

        Row totalUsersRow = sheet.createRow(rowNum++);
        totalUsersRow.createCell(0).setCellValue("총 이용자 수");
        totalUsersRow.createCell(1).setCellValue(summary.userActivity().totalUsers());

        Row activeUsersRow = sheet.createRow(rowNum++);
        activeUsersRow.createCell(0).setCellValue("활성 이용자 수");
        activeUsersRow.createCell(1).setCellValue(summary.userActivity().activeUsers());

        Row newUsersRow = sheet.createRow(rowNum++);
        newUsersRow.createCell(0).setCellValue("신규 가입자 수");
        newUsersRow.createCell(1).setCellValue(summary.userActivity().newUsers());

        rowNum++; // 빈 줄

        // 대화 요약
        Row convHeader = sheet.createRow(rowNum++);
        Cell convHeaderCell = convHeader.createCell(0);
        convHeaderCell.setCellValue("AI 대화 요약");
        convHeaderCell.setCellStyle(headerStyle);

        Row totalConvRow = sheet.createRow(rowNum++);
        totalConvRow.createCell(0).setCellValue("총 대화 수");
        totalConvRow.createCell(1).setCellValue(summary.conversation().totalConversations());

        Row avgConvRow = sheet.createRow(rowNum++);
        avgConvRow.createCell(0).setCellValue("일평균 대화 수");
        avgConvRow.createCell(1).setCellValue(String.format("%.1f", summary.conversation().dailyAvgConversations()));

        Row totalMsgRow = sheet.createRow(rowNum++);
        totalMsgRow.createCell(0).setCellValue("총 메시지 수");
        totalMsgRow.createCell(1).setCellValue(summary.conversation().totalMessages());

        // 컬럼 너비 조정
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 5000);
    }

    private void createDailyStatsSheet(Workbook workbook, List<DailyStatResponse> dailyStats) {
        Sheet sheet = workbook.createSheet("일별 통계");

        CellStyle headerStyle = createHeaderStyle(workbook);

        // 헤더
        Row headerRow = sheet.createRow(0);
        String[] headers = {"날짜", "활성 이용자", "대화 수", "평균 감정 점수"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 데이터
        int rowNum = 1;
        for (DailyStatResponse stat : dailyStats) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(stat.date().toString());
            row.createCell(1).setCellValue(stat.activeUsers());
            row.createCell(2).setCellValue(stat.conversations());
            row.createCell(3).setCellValue(String.format("%.1f", stat.avgEmotionScore()));
        }

        // 컬럼 너비 자동 조정
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
