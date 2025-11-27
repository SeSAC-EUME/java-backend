package com.project.eume.domain.repository;

import com.project.eume.domain.entity.EumeUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EumeUserRepository extends JpaRepository<EumeUser, Long> {
    Optional<EumeUser> findByEmail(String email);
}
