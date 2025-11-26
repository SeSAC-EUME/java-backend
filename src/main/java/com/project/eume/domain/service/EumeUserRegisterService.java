package com.project.eume.domain.service;

import com.project.eume.domain.entity.EumeUser;
import com.project.eume.domain.enums.LoginPlatform;
import com.project.eume.domain.repository.EumeUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class EumeUserRegisterService {
    private final EumeUserRepository eumeUserRepository;

    @Transactional
    public EumeUser toEntityAndSave(String email, String name, LoginPlatform loginPlatform) {
        EumeUser user = EumeUser.ofNewRegistration(email, name, loginPlatform);
        return eumeUserRepository.save(user);
    }
}
