package com.stp.stPlatform.service;

import com.stp.stPlatform.domain.VerificationType;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.model.VerificationCode;

public interface VerificationCodeService {
    VerificationCode sendVerificationCode(User user, VerificationType verificationType);
    VerificationCode getVerificationCodeById(Long id);
    VerificationCode getVerificationCodeByUser(Long userId);
    void deleteVerificationCodeById(Long id);
}