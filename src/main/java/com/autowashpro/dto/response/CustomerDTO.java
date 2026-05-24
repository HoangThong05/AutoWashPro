package com.autowashpro.dto.response;

import com.autowashpro.entity.Customer;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CustomerDTO {
    private Integer customerId;
    private Integer userId;
    private String fullName;
    private String email;
    private String phone;
    private String tierName;
    private Integer loyaltyPoints;
    private Integer totalWashes;
    private BigDecimal totalSpend;

    public static CustomerDTO fromEntity(Customer c) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(c.getCustomerId());
        dto.setUserId(c.getUser().getUserId());
        dto.setFullName(c.getUser().getFullName());
        dto.setEmail(c.getUser().getEmail());
        dto.setPhone(c.getUser().getPhone());
        dto.setTierName(c.getTier() != null ? c.getTier().getName() : "Member");
        dto.setLoyaltyPoints(c.getLoyaltyPoints());
        dto.setTotalWashes(c.getTotalWashes());
        dto.setTotalSpend(c.getTotalSpend());
        return dto;
    }
}