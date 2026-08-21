package com.stp.stPlatform.model;

import com.stp.stPlatform.domain.VerificationType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ForgotPasswordToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String otp;

    @Enumerated(EnumType.STRING)
    private VerificationType verificationType;

    private String sendTo;
}