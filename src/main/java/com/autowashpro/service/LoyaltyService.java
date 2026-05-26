package com.autowashpro.service;

import com.autowashpro.dto.request.ManualPointsRequest;
import com.autowashpro.dto.response.LoyaltyResponse;
import com.autowashpro.dto.response.RewardResponse;
import com.autowashpro.entity.*;
import com.autowashpro.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoyaltyService {

    private final CustomerRepository customerRepository;
    private final LoyaltyLogRepository loyaltyLogRepository;
    private final RewardRepository rewardRepository;
    private final RewardRedemptionRepository redemptionRepository;
    private final UserRepository userRepository;

    /* ===== XEM ĐIỂM + LỊCH SỬ ===== */
    public LoyaltyResponse getMyLoyalty(String email) {
        Customer customer = customerRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<LoyaltyLog> logs = loyaltyLogRepository
                .findByCustomer_CustomerIdOrderByCreatedAtDesc(customer.getCustomerId());

        List<LoyaltyResponse.LogDetail> logDetails = logs.stream()
                .map(l -> LoyaltyResponse.LogDetail.builder()
                        .pointsChange(l.getPointsChange())
                        .reason(l.getReason())
                        .refType(l.getRefType())
                        .createdAt(l.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return LoyaltyResponse.builder()
                .customerId(customer.getCustomerId())
                .customerName(customer.getUser().getFullName())
                .loyaltyPoints(customer.getLoyaltyPoints())
                .logs(logDetails)
                .build();
    }

    /* ===== TỰ ĐỘNG CỘNG ĐIỂM SAU THANH TOÁN ===== */
    @Transactional
    public void addPointsAfterPayment(Customer customer, int finalAmount) {
        MemberTier tier = customer.getTier();
        int rate = (tier != null) ? tier.getPointsPer100kVnd() : 1;
        int points = (finalAmount / 100000) * rate;

        if (points <= 0) return;

        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + points);
        customer.setTotalSpend(customer.getTotalSpend()
                .add(java.math.BigDecimal.valueOf(finalAmount)));
        customer.setTotalWashes(customer.getTotalWashes() + 1);
        customerRepository.save(customer);

        LoyaltyLog log = new LoyaltyLog();
        log.setCustomer(customer);
        log.setPointsChange(points);
        log.setReason("Tích điểm từ đơn hàng");
        log.setRefType("BOOKING");
        loyaltyLogRepository.save(log);
    }

    /* ===== THỦ CÔNG CỘNG/TRỪ ĐIỂM (Staff/Admin) ===== */
    @Transactional
    public LoyaltyResponse adjustPointsManually(ManualPointsRequest req, String staffEmail) {
        Customer customer = customerRepository.findById(req.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        int newPoints = customer.getLoyaltyPoints() + req.getPoints();
        if (newPoints < 0) throw new RuntimeException("Điểm không đủ để trừ");

        customer.setLoyaltyPoints(newPoints);
        customerRepository.save(customer);

        LoyaltyLog log = new LoyaltyLog();
        log.setCustomer(customer);
        log.setPointsChange(req.getPoints());
        log.setReason(req.getReason());
        log.setRefType("MANUAL");
        log.setCreatedBy(staff);
        loyaltyLogRepository.save(log);

        return getMyLoyaltyById(customer.getCustomerId());
    }

    /* ===== XEM DANH SÁCH PHẦN THƯỞNG ===== */
    public List<RewardResponse> getRewards() {
        return rewardRepository.findByIsActiveTrueOrderByPointsRequiredAsc()
                .stream()
                .map(this::toRewardResponse)
                .collect(Collectors.toList());
    }

    /* ===== ĐỔI QUÀ ===== */
    @Transactional
    public String redeemReward(String email, Integer rewardId) {
        Customer customer = customerRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        if (!reward.getIsActive()) {
            throw new RuntimeException("Phần thưởng không còn khả dụng");
        }
        if (customer.getLoyaltyPoints() < reward.getPointsRequired()) {
            throw new RuntimeException("Điểm không đủ. Cần "
                    + reward.getPointsRequired() + " điểm, bạn có "
                    + customer.getLoyaltyPoints() + " điểm");
        }
        if (reward.getStock() != null && reward.getStock() <= 0) {
            throw new RuntimeException("Phần thưởng đã hết hàng");
        }

        // Trừ điểm
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() - reward.getPointsRequired());
        customerRepository.save(customer);

        // Giảm stock
        if (reward.getStock() != null) {
            reward.setStock(reward.getStock() - 1);
            rewardRepository.save(reward);
        }

        // Ghi log
        LoyaltyLog log = new LoyaltyLog();
        log.setCustomer(customer);
        log.setPointsChange(-reward.getPointsRequired());
        log.setReason("Đổi quà: " + reward.getName());
        log.setRefType("REDEEM");
        log.setRefId(rewardId);
        loyaltyLogRepository.save(log);

        // Tạo redemption
        RewardRedemption redemption = new RewardRedemption();
        redemption.setCustomer(customer);
        redemption.setReward(reward);
        redemption.setPointsSpent(reward.getPointsRequired());
        redemption.setStatus("PENDING");
        redemptionRepository.save(redemption);

        return "Đổi quà thành công! Còn lại "
                + customer.getLoyaltyPoints() + " điểm";
    }

    /* ===== XEM LỊCH SỬ ĐỔI QUÀ ===== */
    public List<RewardRedemption> getMyRedemptions(String email) {
        Customer customer = customerRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return redemptionRepository
                .findByCustomer_CustomerIdOrderByRedeemedAtDesc(customer.getCustomerId());
    }

    // ===== HELPERS =====
    private LoyaltyResponse getMyLoyaltyById(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        List<LoyaltyLog> logs = loyaltyLogRepository
                .findByCustomer_CustomerIdOrderByCreatedAtDesc(customerId);
        return LoyaltyResponse.builder()
                .customerId(customer.getCustomerId())
                .customerName(customer.getUser().getFullName())
                .loyaltyPoints(customer.getLoyaltyPoints())
                .logs(logs.stream().map(l -> LoyaltyResponse.LogDetail.builder()
                        .pointsChange(l.getPointsChange())
                        .reason(l.getReason())
                        .refType(l.getRefType())
                        .createdAt(l.getCreatedAt())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    private RewardResponse toRewardResponse(Reward r) {
        return RewardResponse.builder()
                .rewardId(r.getRewardId())
                .name(r.getName())
                .description(r.getDescription())
                .pointsRequired(r.getPointsRequired())
                .rewardType(r.getRewardType())
                .discountValue(r.getDiscountValue())
                .isActive(r.getIsActive())
                .stock(r.getStock())
                .expiresAt(r.getExpiresAt())
                .build();
    }
}