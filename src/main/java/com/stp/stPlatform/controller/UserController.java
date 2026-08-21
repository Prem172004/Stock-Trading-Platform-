package com.stp.stPlatform.controller;

import com.stp.stPlatform.domain.VerificationType;
import com.stp.stPlatform.model.ForgotPasswordToken;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.model.VerificationCode;
import com.stp.stPlatform.request.ForgotPasswordTokenRequest;
import com.stp.stPlatform.request.ResetPasswordRequest;
import com.stp.stPlatform.response.ApiResponse;
import com.stp.stPlatform.response.AuthResponse;
import com.stp.stPlatform.service.EmailService;
import com.stp.stPlatform.service.ForgotPasswordService;
import com.stp.stPlatform.service.UserService;
import com.stp.stPlatform.service.VerificationCodeService;
import com.stp.stPlatform.utils.OtpUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;
    private final ForgotPasswordService forgotPasswordService;

    public UserController(UserService userService,
                          EmailService emailService,
                          VerificationCodeService verificationCodeService,
                          ForgotPasswordService forgotPasswordService) {
        this.userService = userService;
        this.emailService = emailService;
        this.verificationCodeService = verificationCodeService;
        this.forgotPasswordService = forgotPasswordService;
    }

    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) {
        User user = userService.findUserProfileByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(
            @RequestHeader("Authorization") String jwt,
            @PathVariable VerificationType verificationType) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        if (verificationCode == null) {
            verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
        }

        if (verificationType.equals(VerificationType.EMAIL)) {
            emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
        }

        return new ResponseEntity<>("Verification OTP sent successfully", HttpStatus.OK);
    }

    @PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String otp) {

        User user = userService.findUserProfileByJwt(jwt);
        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        if (verificationCode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification code not found");
        }

        String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL)
                ? verificationCode.getEmail()
                : String.valueOf(user.getId());

        boolean isVerified = verificationCode.getOtp().equals(otp);
        if (!isVerified) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP code");
        }

        User updatedUser = userService.enableTwoFactorAuthentication(
                verificationCode.getVerificationType(),
                sendTo,
                user
        );

        verificationCodeService.deleteVerificationCodeById(verificationCode.getId());

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp(
            @RequestBody ForgotPasswordTokenRequest request) throws Exception {

        User user = userService.findUserByEmail(request.getSendTo());
        String otp = OtpUtils.generateOTP();
        String id = UUID.randomUUID().toString();

        ForgotPasswordToken token = forgotPasswordService.findByUser(user.getId());

        if (token == null) {
            token = forgotPasswordService.createToken(user, id, otp, request.getVerificationType(), request.getSendTo());
        }

        if (request.getVerificationType().equals(VerificationType.EMAIL)) {
            emailService.sendVerificationOtpEmail(user.getEmail(), token.getOtp());
        }

        AuthResponse response = new AuthResponse();
        response.setSession(token.getId());
        response.setMessage("Password reset OTP sent successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword(
            @RequestParam String id,
            @RequestBody ResetPasswordRequest req) {

        ForgotPasswordToken token = forgotPasswordService.findById(id);

        if (token == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset session");
        }

        boolean isVerified = token.getOtp().equals(req.getOtp());
        if (!isVerified) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP code");
        }

        userService.updatePassword(token.getUser(), req.getPassword());
        forgotPasswordService.deleteToken(token);

        ApiResponse res = new ApiResponse("Password reset successfully", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}