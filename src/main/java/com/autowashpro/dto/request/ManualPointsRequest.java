package com.autowashpro.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ManualPointsRequest {

    @NotNull
    private Integer customerId;

    @NotNull
    private Integer points; // dương = cộng, âm = trừ

    @NotBlank
    private String reason;
}