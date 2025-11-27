package com.project.eume.domain.service;

import com.project.eume.domain.entity.Sigungu;
import com.project.eume.domain.repository.SigunguRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SigunguSearchService {
    private final SigunguRepository sigunguRepository;

    public Sigungu findByIdOrNull(Long id) {
        if (id == null) {
            return null;
        }
        return sigunguRepository.findById(id).orElse(null);
    }

    public List<Sigungu> findAll() {
        return sigunguRepository.findAll();
    }
}
