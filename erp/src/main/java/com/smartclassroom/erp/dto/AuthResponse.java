package com.smartclassroom.erp.dto;

import com.smartclassroom.erp.entity.User;

public record AuthResponse(
        String token,
        String tokenType,
        Long id,
        String name,
        String email,
        User.Role role
) {
    public static AuthResponse from(String token, User user) {
        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
