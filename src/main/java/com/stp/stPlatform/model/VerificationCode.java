package com.stp.stPlatform.model;

import com.stp.stPlatform.domain.VerificationType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String otp;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String email;

    @Enumerated(EnumType.STRING)
    private VerificationType verificationType;
}