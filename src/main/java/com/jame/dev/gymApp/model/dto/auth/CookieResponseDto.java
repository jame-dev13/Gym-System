package com.jame.dev.gymApp.model.dto.auth;

import lombok.Builder;

@Builder
public record CookieResponseDto(
        String access, String refresh
) {
}
