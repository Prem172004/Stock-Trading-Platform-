package com.stp.stPlatform.service;

import com.stp.stPlatform.domain.VerificationType;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.model.VerificationCode;
import com.stp.stPlatform.repository.VerificationCodeRepository;
import com.stp.stPlatform.utils.OtpUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;

    public VerificationCodeServiceImpl(VerificationCodeRepository verificationCodeRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
    }

    @Override
    public VerificationCode sendVerificationCode(User user, VerificationType verificationType) {
        VerificationCode verificationCode1 = new VerificationCode();
        verificationCode1.setOtp(OtpUtils.generateOTP());
        verificationCode1.setVerificationType(verificationType);
        verificationCode1.setEmail(user.getEmail());
        verificationCode1.setUser(user);

        return verificationCodeRepository.save(verificationCode1);
    }

    @Override
    public VerificationCode getVerificationCodeById(Long id) {
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findById(id);
        return verificationCode.orElse(null);
    }

    @Override
    public VerificationCode getVerificationCodeByUser(Long userId) {
        return verificationCodeRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public void deleteVerificationCodeById(Long id) {
        verificationCodeRepository.deleteById(id);
    }
}