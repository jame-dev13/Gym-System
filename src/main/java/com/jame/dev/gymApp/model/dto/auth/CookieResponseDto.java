package com.jame.dev.gymApp.model.dto.auth;

import lombok.Builder;
import org.springframework.http.ResponseCookie;

@Builder
public record CookieResponseDto(
        ResponseCookie access, ResponseCookie refresh
) {
}
