package com.stp.stPlatform.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.List;

@Data
@Embeddable
public class SparklineIn7d {

    @ElementCollection
    @JsonProperty("price")
    private List<Double> price;
}