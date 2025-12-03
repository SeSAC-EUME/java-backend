package com.project.eume.domain.dto.response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * 감정 분포 통계 API 응답 DTO
 * 감정 분포 차트를 위한 집계 데이터 반환
 */
public record AdminEmotionStatisticsResponse(
        Period period,
        long totalUsers,
        long usersWithEmotionData,
        long usersWithoutEmotionData,
        Distribution distribution,
        Summary summary
) {
    public static AdminEmotionStatisticsResponse of(
            LocalDate startDate,
            LocalDate endDate,
            long totalUsers,
            long safe,
            long caution,
            long highRisk,
            long critical,
            long noData
    ) {
        Distribution dist = Distribution.of(totalUsers, safe, caution, highRisk, critical, noData);
        Summary sum = new Summary(safe, caution, highRisk + critical);

        return new AdminEmotionStatisticsResponse(
                new Period(startDate, endDate),
                totalUsers,
                totalUsers - noData,
                noData,
                dist,
                sum
        );
    }

    public record Period(
            LocalDate startDate,
            LocalDate endDate
    ) {}

    public record Distribution(
            DistributionItem safe,
            DistributionItem caution,
            DistributionItem highRisk,
            DistributionItem critical,
            DistributionItem noData
    ) {
        public static Distribution of(long total, long safe, long caution, long highRisk, long critical, long noData) {
            return new Distribution(
                    new DistributionItem("안전", "0-29", safe, calculatePercentage(safe, total)),
                    new DistributionItem("주의", "30-59", caution, calculatePercentage(caution, total)),
                    new DistributionItem("고위험", "60-79", highRisk, calculatePercentage(highRisk, total)),
                    new DistributionItem("매우 심각", "80-100", critical, calculatePercentage(critical, total)),
                    new DistributionItem("데이터 없음", null, noData, calculatePercentage(noData, total))
            );
        }

        private static double calculatePercentage(long count, long total) {
            if (total == 0) return 0.0;
            return BigDecimal.valueOf(count * 100.0 / total)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
        }
    }

    public record DistributionItem(
            String label,
            String scoreRange,
            long count,
            double percentage
    ) {}

    /**
     * 대시보드 카드용 요약 통계
     * danger = highRisk + critical
     */
    public record Summary(
            long safe,
            long caution,
            long danger
    ) {}
}
