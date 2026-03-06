package com.marketflow.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(

        @JsonProperty("refresh_token")
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}