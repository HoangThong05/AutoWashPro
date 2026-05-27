package com.autowashpro.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContactResponse {
    private Integer contactId;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private String handledBy;
    private LocalDateTime handledAt;
    private LocalDateTime createdAt;
    private List<ContactMessageResponse> messages;
}