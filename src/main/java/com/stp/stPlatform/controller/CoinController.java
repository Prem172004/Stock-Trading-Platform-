package com.stp.stPlatform.controller;

import com.stp.stPlatform.model.Coin;
import com.stp.stPlatform.service.CoinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/coins")
public class CoinController {

    private final CoinService coinService;
    private final ObjectMapper objectMapper;

    public CoinController(CoinService coinService, ObjectMapper objectMapper) {
        this.coinService = coinService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<List<Coin>> getCoinList(
            @RequestParam(required = false, defaultValue = "1") int page) throws Exception {
        List<Coin> coins = coinService.getCoinList(page);
        return new ResponseEntity<>(coins, HttpStatus.OK);
    }

    @GetMapping("/{coinId}/chart")
    public ResponseEntity<JsonNode> getMarketChart(
            @PathVariable String coinId,
            @RequestParam(required = false, defaultValue = "1") int days) throws Exception {
        String response = coinService.getMarketChart(coinId, days);
        JsonNode jsonNode = objectMapper.readTree(response);
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<JsonNode> searchCoin(
            @RequestParam("q") String keyword) throws Exception {
        String response = coinService.searchCoin(keyword);
        JsonNode jsonNode = objectMapper.readTree(response);
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }

    @GetMapping("/top50")
    public ResponseEntity<JsonNode> getTop50CoinsByMarketCapRank() throws Exception {
        String response = coinService.getTop50CoinsByMarketCapRank();
        JsonNode jsonNode = objectMapper.readTree(response);
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }

    @GetMapping("/trending")
    public ResponseEntity<JsonNode> getTrendingCoins() throws Exception {
        String response = coinService.getTradingCoins();
        JsonNode jsonNode = objectMapper.readTree(response);
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }

    @GetMapping("/details/{coinId}")
    public ResponseEntity<JsonNode> getCoinDetails(
            @PathVariable String coinId) throws Exception {
        String response = coinService.getCoinDetails(coinId);
        JsonNode jsonNode = objectMapper.readTree(response);
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }

    @GetMapping("/{coinId}")
    public ResponseEntity<Coin> findById(
            @PathVariable String coinId) throws Exception {
        Coin coin = coinService.findById(coinId);
        return new ResponseEntity<>(coin, HttpStatus.OK);
    }
}