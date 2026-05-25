package com.autowashpro.service;

import com.autowashpro.dto.request.PromotionRequest;
import com.autowashpro.dto.response.PromotionResponse;
import com.autowashpro.entity.Promotion;
import com.autowashpro.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    /* ===== LẤY TẤT CẢ ===== */
    public List<PromotionResponse> getAll() {
        return promotionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /* ===== LẤY ĐANG ACTIVE ===== */
    public List<PromotionResponse> getActive() {
        return promotionRepository.findAll()
                .stream()
                .filter(p -> p.getIsActive()
                        && LocalDateTime.now().isAfter(p.getStartAt())
                        && LocalDateTime.now().isBefore(p.getEndAt()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /* ===== KIỂM TRA MÃ ===== */
    public PromotionResponse checkCode(String code) {
        Promotion promotion = promotionRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không hợp lệ"));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(promotion.getStartAt())) {
            throw new RuntimeException("Mã giảm giá chưa có hiệu lực");
        }
        if (now.isAfter(promotion.getEndAt())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn");
        }
        if (promotion.getUsageLimit() != null
                && promotion.getUsedCount() >= promotion.getUsageLimit()) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng");
        }

        return toResponse(promotion);
    }

    /* ===== TẠO MỚI ===== */
    @Transactional
    public PromotionResponse create(PromotionRequest req) {
        if (promotionRepository.findByCodeAndIsActiveTrue(req.getCode()).isPresent()) {
            throw new RuntimeException("Mã giảm giá đã tồn tại");
        }

        Promotion promotion = new Promotion();
        promotion.setCode(req.getCode().toUpperCase().trim());
        promotion.setName(req.getName());
        promotion.setDiscountType(req.getDiscountType());
        promotion.setDiscountValue(req.getDiscountValue());
        promotion.setMinOrderValue(req.getMinOrderValue() != null
                ? req.getMinOrderValue() : java.math.BigDecimal.ZERO);
        promotion.setMaxDiscount(req.getMaxDiscount());
        promotion.setUsageLimit(req.getUsageLimit());
        promotion.setUsedCount(0);
        promotion.setStartAt(req.getStartAt());
        promotion.setEndAt(req.getEndAt());
        promotion.setIsActive(true);

        return toResponse(promotionRepository.save(promotion));
    }

    /* ===== CẬP NHẬT ===== */
    @Transactional
    public PromotionResponse update(Integer id, PromotionRequest req) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        promotion.setName(req.getName());
        promotion.setDiscountType(req.getDiscountType());
        promotion.setDiscountValue(req.getDiscountValue());
        promotion.setMinOrderValue(req.getMinOrderValue() != null
                ? req.getMinOrderValue() : java.math.BigDecimal.ZERO);
        promotion.setMaxDiscount(req.getMaxDiscount());
        promotion.setUsageLimit(req.getUsageLimit());
        promotion.setStartAt(req.getStartAt());
        promotion.setEndAt(req.getEndAt());

        return toResponse(promotionRepository.save(promotion));
    }

    /* ===== VÔ HIỆU HÓA ===== */
    @Transactional
    public void deactivate(Integer id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        promotion.setIsActive(false);
        promotionRepository.save(promotion);
    }

    private PromotionResponse toResponse(Promotion p) {
        return PromotionResponse.builder()
                .promotionId(p.getPromotionId())
                .code(p.getCode())
                .name(p.getName())
                .discountType(p.getDiscountType())
                .discountValue(p.getDiscountValue())
                .minOrderValue(p.getMinOrderValue())
                .maxDiscount(p.getMaxDiscount())
                .usageLimit(p.getUsageLimit())
                .usedCount(p.getUsedCount())
                .startAt(p.getStartAt())
                .endAt(p.getEndAt())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .build();
    }
}