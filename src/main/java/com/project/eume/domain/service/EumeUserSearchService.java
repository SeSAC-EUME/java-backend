package com.project.eume.domain.service;

import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.repository.EumeUserRepository;
import com.project.eume.exceptions.errorcode.EumeUserErrorCode;
import com.project.eume.exceptions.exception.EumeUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class EumeUserSearchService {
    private final EumeUserRepository eumeUserRepository;

    public Optional<EumeUser> findByEmailOrOptional(String email) {
        return eumeUserRepository.findByEmail(email);
    }

    public EumeUser findByEmail(String email) {
        return eumeUserRepository.findByEmail(email)
            .orElseThrow(() -> new EumeUserException(EumeUserErrorCode.NOT_EXIST));
    }
}
