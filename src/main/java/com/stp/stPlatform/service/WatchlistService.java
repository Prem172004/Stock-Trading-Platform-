package com.stp.stPlatform.service;

import com.stp.stPlatform.model.Coin;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.model.Watchlist;

public interface WatchlistService {
    Watchlist findUserWatchlist(Long userId) throws Exception;
    Watchlist createWatchlist(User user);
    Watchlist findById(Long id) throws Exception;
    Coin addItemToWatchlist(Coin coin, User user) throws Exception;
}