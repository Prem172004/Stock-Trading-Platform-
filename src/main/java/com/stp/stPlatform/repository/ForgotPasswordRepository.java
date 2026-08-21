package com.stp.stPlatform.repository;

import com.stp.stPlatform.model.ForgotPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordToken, String> {
    Optional<ForgotPasswordToken> findByUserId(Long userId);
}