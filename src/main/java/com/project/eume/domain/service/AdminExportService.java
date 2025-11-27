package com.project.eume.domain.service;

import com.project.eume.domain.entity.EumeUser;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AdminExportService {

    private final AdminSearchService adminSearchService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 사용자 목록을 Excel 파일로 내보내기
     *
     * @param status  상태 필터 (nullable)
     * @param keyword 검색어 (nullable)
     * @return Excel 파일 바이트 배열
     */
    public byte[] exportUsersToExcel(String status, String keyword) {
        List<EumeUser> users = adminSearchService.findAllUsers(status, keyword);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("이용자 목록");

            // 헤더 스타일 생성
            CellStyle headerStyle = createHeaderStyle(workbook);

            // 헤더 생성
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "이메일", "이름", "닉네임", "상태", "지역", "마지막 로그인", "가입일"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행 생성
            int rowNum = 1;
            for (EumeUser user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getEmail());
                row.createCell(2).setCellValue(user.getUserName() != null ? user.getUserName() : "");
                row.createCell(3).setCellValue(user.getNickname() != null ? user.getNickname() : "");
                row.createCell(4).setCellValue(convertStatus(user.getUserStatus()));
                row.createCell(5).setCellValue(getSigunguName(user));
                row.createCell(6).setCellValue(user.getLastLoginDate() != null ? user.getLastLoginDate().format(DATE_FORMATTER) : "");
                row.createCell(7).setCellValue(user.getCreatedAt() != null ? user.getCreatedAt().format(DATE_FORMATTER) : "");
            }

            // 컬럼 너비 자동 조정
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Excel export failed", e);
            throw new AdminException(AdminErrorCode.EXPORT_FAILED);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private String convertStatus(String status) {
        if (status == null) return "";
        return switch (status) {
            case "ACTIVE" -> "활성";
            case "DEACTIVATED" -> "비활성";
            case "WITHDRAWN" -> "탈퇴";
            default -> status;
        };
    }

    private String getSigunguName(EumeUser user) {
        if (user.getSigungu() == null) return "";
        return user.getSigungu().getSido() + " " + user.getSigungu().getSigungu();
    }
}
