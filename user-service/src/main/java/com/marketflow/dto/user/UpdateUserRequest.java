package com.marketflow.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(

        @NotBlank(message = "Name is required")
        String name,

        String phone
) {}