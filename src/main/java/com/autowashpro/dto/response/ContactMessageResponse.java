package com.autowashpro.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContactMessageResponse {
    private Integer messageId;
    private Integer contactId;
    private String senderType;
    private String senderName;
    private String message;
    private LocalDateTime createdAt;
}