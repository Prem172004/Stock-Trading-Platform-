package com.stp.stPlatform.service;

import com.stp.stPlatform.model.TwoFactorOTP;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.repository.TwoFactorOtpRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TwoFactorOtpServiceImpl implements TwoFactorOtpService {

    private final TwoFactorOtpRepository twoFactorOtpRepository;

    public TwoFactorOtpServiceImpl(TwoFactorOtpRepository twoFactorOtpRepository) {
        this.twoFactorOtpRepository = twoFactorOtpRepository;
    }

    @Override
    public TwoFactorOTP createTwoFactorOtp(User user, String otp, String jwt) {
        // Clear any existing OTP session for this user
        TwoFactorOTP existingOtp = findByUser(user.getId());
        if (existingOtp != null) {
            deleteTwoFactorOtp(existingOtp);
        }

        TwoFactorOTP twoFactorOTP = new TwoFactorOTP();
        twoFactorOTP.setId(UUID.randomUUID().toString());
        twoFactorOTP.setOtp(otp);
        twoFactorOTP.setJwt(jwt);
        twoFactorOTP.setUser(user);

        return twoFactorOtpRepository.save(twoFactorOTP);
    }

    @Override
    public TwoFactorOTP findByUser(Long userId) {
        return twoFactorOtpRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public TwoFactorOTP findById(String id) {
        Optional<TwoFactorOTP> otp = twoFactorOtpRepository.findById(id);
        return otp.orElse(null);
    }

    @Override
    public boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOTP, String otp) {
        if (twoFactorOTP == null || twoFactorOTP.getOtp() == null || otp == null) {
            return false;
        }
        return twoFactorOTP.getOtp().equals(otp.trim());
    }

    @Override
    public void deleteTwoFactorOtp(TwoFactorOTP twoFactorOTP) {
        if (twoFactorOTP != null) {
            twoFactorOtpRepository.delete(twoFactorOTP);
        }
    }
}