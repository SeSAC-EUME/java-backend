package com.project.eume.domain.repository;

import com.project.eume.domain.entity.EumeAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EumeAdminRepository extends JpaRepository<EumeAdmin, Long> {
    Optional<EumeAdmin> findByAdminLoginId(String adminLoginId);
}
