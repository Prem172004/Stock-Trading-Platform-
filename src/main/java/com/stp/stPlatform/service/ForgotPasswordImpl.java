package com.stp.stPlatform.service;

import com.stp.stPlatform.domain.VerificationType;
import com.stp.stPlatform.model.ForgotPasswordToken;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.repository.ForgotPasswordRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForgotPasswordImpl implements ForgotPasswordService {

    private final ForgotPasswordRepository forgotPasswordRepository;

    public ForgotPasswordImpl(ForgotPasswordRepository forgotPasswordRepository) {
        this.forgotPasswordRepository = forgotPasswordRepository;
    }

    @Override
    public ForgotPasswordToken createToken(User user, String id, String otp,
                                           VerificationType verificationType,
                                           String sendTo) {
        // Remove existing token if the user requests a new OTP
        ForgotPasswordToken existingToken = findByUser(user.getId());
        if (existingToken != null) {
            deleteToken(existingToken);
        }

        ForgotPasswordToken token = new ForgotPasswordToken();
        token.setUser(user);
        token.setSendTo(sendTo);
        token.setVerificationType(verificationType);
        token.setOtp(otp);
        if (id != null) {
            token.setId(id);
        }
        return forgotPasswordRepository.save(token);
    }

    @Override
    public ForgotPasswordToken findById(String id) {
        Optional<ForgotPasswordToken> token = forgotPasswordRepository.findById(id);
        return token.orElse(null);
    }

    @Override
    public ForgotPasswordToken findByUser(Long userId) {
        return forgotPasswordRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public void deleteToken(ForgotPasswordToken token) {
        if (token != null) {
            forgotPasswordRepository.delete(token);
        }
    }
}