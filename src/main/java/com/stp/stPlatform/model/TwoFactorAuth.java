package com.stp.stPlatform.model;

import com.stp.stPlatform.domain.VerificationType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
@Embeddable
public class TwoFactorAuth {
    private boolean isEnabled = false;

    @Enumerated(EnumType.STRING)
    private VerificationType sendTo;

}
