package com.stp.stPlatform.service;

import com.stp.stPlatform.model.Asset;
import com.stp.stPlatform.model.Coin;
import com.stp.stPlatform.model.User;

import java.util.List;

public interface AssetService {
    Asset createAsset(User user, Coin coin, double quantity);
    Asset getAssetById(Long assetId);
    Asset getAssetByUserIdAndId(Long userId, Long assetId);
    List<Asset> getUsersAssets(Long userId);
    Asset updateAsset(Long assetId, double quantity);
    Asset findAssetByUserIdAndCoinId(Long userId, String coinId);
    void deleteAsset(Long assetId);
}