package com.project.eume.domain.service;

import com.project.eume.domain.entity.Sigungu;
import com.project.eume.domain.repository.SigunguRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SigunguSearchService {
    private final SigunguRepository SigunguRepository;

    public Sigungu findByIdOrNull(Long id) {
        if (id == null) {
            return null;
        }
        return SigunguRepository.findById(id).orElse(null);
    }
}
