package com.autowashpro.dto.response;

import com.autowashpro.entity.MemberTier;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MemberTierDTO {
    private Integer tierId;
    private String name;
    private Integer minPoints;
    private Integer minWashes;
    private BigDecimal minSpend;
    private Integer pointsPer100kVnd;
    private Integer discountPercent;
    private Integer advanceBookingHours;
    private Integer maxBookingsPerDay;
    private Integer priority;

    public static MemberTierDTO fromEntity(MemberTier tier) {
        MemberTierDTO dto = new MemberTierDTO();
        dto.setTierId(tier.getTierId());
        dto.setName(tier.getName());
        dto.setMinPoints(tier.getMinPoints());
        dto.setMinWashes(tier.getMinWashes());
        dto.setMinSpend(tier.getMinSpend());
        dto.setPointsPer100kVnd(tier.getPointsPer100kVnd());
        dto.setDiscountPercent(tier.getDiscountPercent());
        dto.setAdvanceBookingHours(tier.getAdvanceBookingHours());
        dto.setMaxBookingsPerDay(tier.getMaxBookingsPerDay());
        dto.setPriority(tier.getPriority());
        return dto;
    }
}