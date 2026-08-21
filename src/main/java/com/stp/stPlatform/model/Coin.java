package com.stp.stPlatform.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "coins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coin {

    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("name")
    private String name;

    @JsonProperty("image")
    private String image;

    @JsonProperty("current_price")
    private Double currentPrice;

    @JsonProperty("market_cap")
    private Long marketCap;

    @JsonProperty("market_cap_rank")
    private Integer marketCapRank;

    @JsonProperty("fully_diluted_valuation")
    private Long fullyDilutedValuation;

    @JsonProperty("total_volume")
    private Long totalVolume;

    @JsonProperty("high_24h")
    private Double high24h;

    @JsonProperty("low_24h")
    private Double low24h;

    @JsonProperty("price_change_24h")
    private Double priceChange24h;

    @JsonProperty("price_change_percentage_24h")
    private Double priceChangePercentage24h;

    @JsonProperty("market_cap_change_24h")
    private Double marketCapChange24h;

    @JsonProperty("market_cap_change_percentage_24h")
    private Double marketCapChangePercentage24h;

    @JsonProperty("circulating_supply")
    private Double circulatingSupply;

    @JsonProperty("total_supply")
    private Double totalSupply;

    @JsonProperty("max_supply")
    private Double maxSupply;

    @JsonProperty("ath")
    private Double ath;

    @JsonProperty("ath_change_percentage")
    private Double athChangePercentage;

    @JsonProperty("ath_date")
    private Date athDate;

    @JsonProperty("atl")
    private Double atl;

    @JsonProperty("atl_change_percentage")
    private Double atlChangePercentage;

    @JsonProperty("atl_date")
    private Date atlDate;

    @Embedded
    @JsonProperty("roi")
    private Roi roi;

    @JsonProperty("last_updated")
    private Date lastUpdated;

    @JsonProperty("market_cap_rank_with_rehypothecated")
    private Integer marketCapRankWithRehypothecated;

    @Embedded
    @JsonProperty("sparkline_in_7d")
    private SparklineIn7d sparklineIn7d;

    @JsonProperty("price_change_percentage_1h_in_currency")
    private Double priceChangePercentage1hInCurrency;

    @JsonProperty("price_change_percentage_24h_in_currency")
    private Double priceChangePercentage24hInCurrency;

    @JsonProperty("price_change_percentage_7d_in_currency")
    private Double priceChangePercentage7dInCurrency;

    @JsonProperty("price_change_percentage_14d_in_currency")
    private Double priceChangePercentage14dInCurrency;

    @JsonProperty("price_change_percentage_30d_in_currency")
    private Double priceChangePercentage30dInCurrency;

    @JsonProperty("price_change_percentage_200d_in_currency")
    private Double priceChangePercentage200dInCurrency;

    @JsonProperty("price_change_percentage_1y_in_currency")
    private Double priceChangePercentage1yInCurrency;
}