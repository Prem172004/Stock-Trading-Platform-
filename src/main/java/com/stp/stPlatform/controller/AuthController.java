package com.stp.stPlatform.controller;

import com.stp.stPlatform.config.JwtProvider;
import com.stp.stPlatform.model.TwoFactorOTP;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.repository.UserRepository;
import com.stp.stPlatform.response.AuthResponse;
import com.stp.stPlatform.service.CustomUserDetailsService;
import com.stp.stPlatform.service.EmailService;
import com.stp.stPlatform.service.TwoFactorOtpService;
import com.stp.stPlatform.utils.OtpUtils;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final TwoFactorOtpService twoFactorOtpService;
    private final EmailService emailService;

    public AuthController(UserRepository userRepository,
                          CustomUserDetailsService customUserDetailsService,
                          TwoFactorOtpService twoFactorOtpService,
                          EmailService emailService) {
        this.userRepository = userRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.twoFactorOtpService = twoFactorOtpService;
        this.emailService = emailService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        Optional<User> isEmailExist = userRepository.findByEmail(user.getEmail());
        if (isEmailExist.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already used with another account");
        }

        User newUser = new User();
        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());

        userRepository.save(newUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("Registration Successful");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws MessagingException {
        String userName = user.getEmail();
        String password = user.getPassword();

        Authentication auth = authenticate(userName, password);
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        User authUser = userRepository.findByEmail(userName)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (authUser.getTwoFactorAuth() != null && authUser.getTwoFactorAuth().isEnabled()) {
            AuthResponse res = new AuthResponse();
            res.setMessage("Two factor authentication is enabled");
            res.setTwoFactorAuthEnabled(true);

            String otp = OtpUtils.generateOTP();

            TwoFactorOTP oldTwoFactorOTP = twoFactorOtpService.findByUser(authUser.getId());
            if (oldTwoFactorOTP != null) {
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOTP);
            }

            TwoFactorOTP newTwoFactorOTP = twoFactorOtpService.createTwoFactorOtp(authUser, otp, jwt);
            emailService.sendVerificationOtpEmail(userName, otp);

            res.setSession(newTwoFactorOTP.getId());

            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        }

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("Login Successful");

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySigningOtp(
            @PathVariable String otp,
            @RequestParam String id) {

        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findById(id);

        if (twoFactorOTP == null || !twoFactorOtpService.verifyTwoFactorOtp(twoFactorOTP, otp)) {
            throw new BadCredentialsException("Invalid or expired OTP");
        }

        AuthResponse res = new AuthResponse();
        res.setJwt(twoFactorOTP.getJwt());
        res.setStatus(true);
        res.setMessage("Two-Factor Authentication Verified");

        twoFactorOtpService.deleteTwoFactorOtp(twoFactorOTP);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    private Authentication authenticate(String userName, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!password.equals(userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}