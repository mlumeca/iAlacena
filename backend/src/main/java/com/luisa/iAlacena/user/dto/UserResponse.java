package com.luisa.iAlacena.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luisa.iAlacena.user.model.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String token,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String refreshToken,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String avatar
) {
    public static UserResponse of(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                null,
                null,
                user.getAvatar()
        );
    }

    public static UserResponse of(User user, String token, String refreshToken) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                token,
                refreshToken,
                user.getAvatar()
        );
    }
}