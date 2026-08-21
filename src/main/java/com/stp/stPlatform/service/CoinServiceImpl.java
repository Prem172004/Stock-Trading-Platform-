package com.stp.stPlatform.service;


import com.stp.stPlatform.model.Coin;
import com.stp.stPlatform.repository.CoinRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

@Service
public class CoinServiceImpl implements CoinService {

    private final CoinRepository coinRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String COINGECKO_BASE_URL = "https://api.coingecko.com/api/v3";

    public CoinServiceImpl(CoinRepository coinRepository, ObjectMapper objectMapper) {
        this.coinRepository = coinRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Coin> getCoinList(int page) throws Exception {
        String url = COINGECKO_BASE_URL + "/coins/markets?vs_currency=usd&per_page=10&page=" + page + "&sparkline=true";

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            List<Coin> coinList = objectMapper.readValue(response.getBody(), new TypeReference<List<Coin>>() {});

            // Sync to local database
            coinRepository.saveAll(coinList);
            return coinList;
        } catch (HttpClientErrorException e) {
            throw new Exception("Error fetching coin list: " + e.getMessage());
        }
    }
    @Override
    public String getMarketChart(String coinId, int days) throws Exception {
        String url = COINGECKO_BASE_URL + "/coins/" + coinId + "/market_chart?vs_currency=usd&days=" + days;

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new Exception("Error fetching market chart: " + e.getMessage());
        }
    }

    @Override
    public String getCoinDetails(String coinId) throws Exception {
        String url = COINGECKO_BASE_URL + "/coins/" + coinId;

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            Coin coin = new Coin();
            coin.setId(jsonNode.get("id").asText());
            coin.setName(jsonNode.get("name").asText());
            coin.setSymbol(jsonNode.get("symbol").asText());
            coin.setImage(jsonNode.get("image").get("large").asText());

            JsonNode marketData = jsonNode.get("market_data");
            coin.setCurrentPrice(marketData.get("current_price").get("usd").asDouble());
            coin.setMarketCap(marketData.get("market_cap").get("usd").asLong());
            coin.setMarketCapRank(marketData.get("market_cap_rank").asInt());
            coin.setTotalVolume(marketData.get("total_volume").get("usd").asLong());
            coin.setHigh24h(marketData.get("high_24h").get("usd").asDouble());
            coin.setLow24h(marketData.get("low_24h").get("usd").asDouble());
            coin.setPriceChange24h(marketData.get("price_change_24h").asDouble());
            coin.setPriceChangePercentage24h(marketData.get("price_change_percentage_24h").asDouble());
            coin.setMarketCapChange24h(marketData.get("market_cap_change_24h").asDouble());
            coin.setMarketCapChangePercentage24h(marketData.get("market_cap_change_percentage_24h").asDouble());
            coin.setTotalSupply(marketData.get("total_supply").asDouble());

            coinRepository.save(coin);

            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new Exception("Error fetching coin details: " + e.getMessage());
        }
    }

    @Override
    public Coin findById(String coinId) throws Exception {
        Optional<Coin> optionalCoin = coinRepository.findById(coinId);
        if (optionalCoin.isEmpty()) {
            throw new Exception("Coin not found with id: " + coinId);
        }
        return optionalCoin.get();
    }

    @Override
    public String searchCoin(String keyword) throws Exception {
        String url = COINGECKO_BASE_URL + "/search?query=" + keyword;

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new Exception("Error searching coin: " + e.getMessage());
        }
    }

    @Override
    public String getTop50CoinsByMarketCapRank() throws Exception {
        String url = COINGECKO_BASE_URL + "/coins/markets?vs_currency=usd&per_page=50&page=1";

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new Exception("Error fetching top 50 coins: " + e.getMessage());
        }
    }

    @Override
    public String getTradingCoins() throws Exception {
        String url = COINGECKO_BASE_URL + "/search/trending";

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new Exception("Error fetching trending coins: " + e.getMessage());
        }
    }
}