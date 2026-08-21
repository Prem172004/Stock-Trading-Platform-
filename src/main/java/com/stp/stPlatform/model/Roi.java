package com.stp.stPlatform.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Roi {

    @JsonProperty("times")
    private Double times;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("percentage")
    private Double percentage;
}