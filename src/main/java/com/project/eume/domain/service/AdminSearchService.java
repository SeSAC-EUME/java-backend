package com.project.eume.domain.service;

import com.project.eume.domain.entity.EumeAdmin;
import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.entity.UserEmotion;
import com.project.eume.domain.repository.EumeAdminRepository;
import com.project.eume.domain.repository.EumeUserRepository;
import com.project.eume.domain.repository.UserEmotionRepository;
import com.project.eume.exceptions.errorcode.AdminErrorCode;
import com.project.eume.exceptions.exception.AdminException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AdminSearchService {
    private final EumeAdminRepository eumeAdminRepository;
    private final EumeUserRepository eumeUserRepository;
    private final UserEmotionRepository userEmotionRepository;

    public EumeAdmin findByAdminLoginId(String adminLoginId) {
        return eumeAdminRepository.findByAdminLoginId(adminLoginId)
                .orElseThrow(() -> new AdminException(AdminErrorCode.ADMIN_NOT_FOUND));
    }

    public Page<EumeUser> findUsers(int page, int size, String status, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        // 기본 조회 (필터링은 추후 QueryDSL 등으로 확장 가능)
        return eumeUserRepository.findAll(pageable);
    }

    public EumeUser findUserById(Long userId) {
        return eumeUserRepository.findById(userId)
                .orElseThrow(() -> new AdminException(AdminErrorCode.USER_NOT_FOUND));
    }

    public Page<UserEmotion> findUserEmotions(Long userId, int page, int size, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);

        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            return userEmotionRepository.findByEumeUserIdAndAnalysisDateBetweenOrderByAnalysisDateDesc(
                    userId, start, end, pageable
            );
        }

        return userEmotionRepository.findByEumeUserIdOrderByAnalysisDateDesc(userId, pageable);
    }


    /**
     * 전체 사용자 조회 (Excel 내보내기용, 페이지네이션 없음)
     *
     * @param status  상태 필터 (nullable)
     * @param keyword 검색어 (nullable)
     * @return 사용자 목록
     */
    public List<EumeUser> findAllUsers(String status, String keyword) {
        // 기본 조회 (필터링은 추후 QueryDSL 등으로 확장 가능)
        if (status != null && !status.isEmpty()) {
            return eumeUserRepository.findByUserStatus(status);
        }
        return eumeUserRepository.findAll();
    }
}
