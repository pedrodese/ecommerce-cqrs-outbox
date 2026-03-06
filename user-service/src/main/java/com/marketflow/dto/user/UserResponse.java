package com.marketflow.dto.user;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.marketflow.domain.entity.User;
import com.marketflow.domain.enums.UserRole;
import com.marketflow.domain.enums.UserStatus;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        UUID id,
        String name,
        String email,
        String phone,
        UserRole role,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public UserResponse(User user) {
        this(
                user.id,
                user.name,
                user.email,
                user.phone,
                user.role,
                user.status,
                user.createdAt,
                user.updatedAt
        );
    }

}