package com.stp.stPlatform.service;

import com.stp.stPlatform.model.Coin;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.model.Watchlist;
import com.stp.stPlatform.repository.WatchlistRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WatchlistServiceImpl implements WatchlistService {

    private final WatchlistRepository watchlistRepository;

    public WatchlistServiceImpl(WatchlistRepository watchlistRepository) {
        this.watchlistRepository = watchlistRepository;
    }

    @Override
    public Watchlist findUserWatchlist(Long userId) throws Exception {
        Optional<Watchlist> optionalWatchlist = watchlistRepository.findByUserId(userId);
        if (optionalWatchlist.isEmpty()) {
            throw new Exception("Watchlist not found for user ID: " + userId);
        }
        return optionalWatchlist.get();
    }

    @Override
    public Watchlist createWatchlist(User user) {
        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        return watchlistRepository.save(watchlist);
    }

    @Override
    public Watchlist findById(Long id) throws Exception {
        Optional<Watchlist> optionalWatchlist = watchlistRepository.findById(id);
        if (optionalWatchlist.isEmpty()) {
            throw new Exception("Watchlist not found with ID: " + id);
        }
        return optionalWatchlist.get();
    }

    @Override
    public Coin addItemToWatchlist(Coin coin, User user) throws Exception {
        Watchlist watchlist = watchlistRepository.findByUserId(user.getId())
                .orElseGet(() -> createWatchlist(user));

        if (watchlist.getCoins().contains(coin)) {
            watchlist.getCoins().remove(coin);
        } else {
            watchlist.getCoins().add(coin);
        }

        watchlistRepository.save(watchlist);
        return coin;
    }
}