package com.autowashpro.controller;

import com.autowashpro.dto.request.ManualPointsRequest;
import com.autowashpro.dto.response.LoyaltyResponse;
import com.autowashpro.dto.response.RewardResponse;
import com.autowashpro.entity.RewardRedemption;
import com.autowashpro.service.LoyaltyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    /** GET /api/loyalty/me — Customer xem điểm + lịch sử */
    @GetMapping("/me")
    public ResponseEntity<LoyaltyResponse> getMyLoyalty(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                loyaltyService.getMyLoyalty(userDetails.getUsername()));
    }

    /** GET /api/loyalty/rewards — Xem danh sách phần thưởng */
    @GetMapping("/rewards")
    public ResponseEntity<List<RewardResponse>> getRewards() {
        return ResponseEntity.ok(loyaltyService.getRewards());
    }

    /** POST /api/loyalty/redeem/{rewardId} — Customer đổi quà */
    @PostMapping("/redeem/{rewardId}")
    public ResponseEntity<String> redeem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer rewardId) {
        return ResponseEntity.ok(
                loyaltyService.redeemReward(userDetails.getUsername(), rewardId));
    }

    /** GET /api/loyalty/redemptions — Lịch sử đổi quà */
    @GetMapping("/redemptions")
    public ResponseEntity<List<RewardRedemption>> getRedemptions(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                loyaltyService.getMyRedemptions(userDetails.getUsername()));
    }

    /** POST /api/loyalty/adjust — Staff/Admin cộng/trừ điểm thủ công */
    @PostMapping("/adjust")
    public ResponseEntity<LoyaltyResponse> adjust(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ManualPointsRequest req) {
        return ResponseEntity.ok(
                loyaltyService.adjustPointsManually(req, userDetails.getUsername()));
    }
}