package com.autowashpro.dto.response;

import com.autowashpro.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Integer userId;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String status;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole().name());
        dto.setStatus(user.getStatus().name());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());
        return dto;
    }
}