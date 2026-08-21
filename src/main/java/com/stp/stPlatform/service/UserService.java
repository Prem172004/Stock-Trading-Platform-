package com.stp.stPlatform.service;

import com.stp.stPlatform.domain.VerificationType;
import com.stp.stPlatform.model.User;

public interface UserService {
    User findUserProfileByJwt(String jwt);
    User findUserByEmail(String email);
    User findUserById(Long userId);

    User enableTwoFactorAuthentication(VerificationType verificationType, String sendTo, User user);
    User disableTwoFactorAuthentication(User user);
    User updatePassword(User user, String newPassword);
}