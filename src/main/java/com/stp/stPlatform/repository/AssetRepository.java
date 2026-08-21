package com.stp.stPlatform.repository;

import com.stp.stPlatform.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByUserId(Long userId);
    Optional<Asset> findByUserIdAndCoinId(Long userId, String coinId);
}