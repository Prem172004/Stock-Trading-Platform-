package com.stp.stPlatform.controller;

import com.stp.stPlatform.model.Coin;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.model.Watchlist;
import com.stp.stPlatform.service.CoinService;
import com.stp.stPlatform.service.UserService;
import com.stp.stPlatform.service.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final UserService userService;
    private final CoinService coinService;

    public WatchlistController(WatchlistService watchlistService,
                               UserService userService,
                               CoinService coinService) {
        this.watchlistService = watchlistService;
        this.userService = userService;
        this.coinService = coinService;
    }

    @GetMapping("/user")
    public ResponseEntity<Watchlist> getUserWatchlist(
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Watchlist watchlist = watchlistService.findUserWatchlist(user.getId());
        return new ResponseEntity<>(watchlist, HttpStatus.OK);
    }

    @GetMapping("/{watchlistId}")
    public ResponseEntity<Watchlist> getWatchlistById(
            @PathVariable Long watchlistId) throws Exception {
        Watchlist watchlist = watchlistService.findById(watchlistId);
        return new ResponseEntity<>(watchlist, HttpStatus.OK);
    }

    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<Coin> addItemToWatchlist(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String coinId) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Coin coin = coinService.findById(coinId);
        Coin addedCoin = watchlistService.addItemToWatchlist(coin, user);
        return new ResponseEntity<>(addedCoin, HttpStatus.OK);
    }
}