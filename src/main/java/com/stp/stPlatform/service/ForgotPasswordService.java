package com.stp.stPlatform.service;

import com.stp.stPlatform.domain.VerificationType;
import com.stp.stPlatform.model.ForgotPasswordToken;
import com.stp.stPlatform.model.User;

public interface ForgotPasswordService {

    ForgotPasswordToken createToken(User user, String id, String otp,
                                    VerificationType verificationType,
                                    String sendTo);

    ForgotPasswordToken findById(String id);

    ForgotPasswordToken findByUser(Long userId);

    void deleteToken(ForgotPasswordToken token);
}