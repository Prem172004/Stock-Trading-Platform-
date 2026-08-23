package com.stp.stPlatform.controller;

import com.stp.stPlatform.model.Asset;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.service.AssetService;
import com.stp.stPlatform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;
    private final UserService userService;

    public AssetController(AssetService assetService, UserService userService) {
        this.assetService = assetService;
        this.userService = userService;
    }

    @GetMapping("/{assetId}")
    public ResponseEntity<Asset> getAssetById(@PathVariable Long assetId) {
        Asset asset = assetService.getAssetById(assetId);
        return new ResponseEntity<>(asset, HttpStatus.OK);
    }

    @GetMapping("/coin/{coinId}/user")
    public ResponseEntity<Asset> getAssetByUserIdAndCoinId(
            @PathVariable String coinId,
            @RequestHeader("Authorization") String jwt) {

        User user = userService.findUserProfileByJwt(jwt);
        Asset asset = assetService.findAssetByUserIdAndCoinId(user.getId(), coinId);

        return new ResponseEntity<>(asset, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Asset>> getAssetsForUser(
            @RequestHeader("Authorization") String jwt) {

        User user = userService.findUserProfileByJwt(jwt);
        List<Asset> assets = assetService.getUsersAssets(user.getId());

        return new ResponseEntity<>(assets, HttpStatus.OK);
    }
}