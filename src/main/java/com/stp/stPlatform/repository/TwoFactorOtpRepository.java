package com.stp.stPlatform.repository;

import com.stp.stPlatform.model.TwoFactorOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TwoFactorOtpRepository extends JpaRepository<TwoFactorOTP, String> {
    Optional<TwoFactorOTP> findByUserId(Long userId);
}