package com.jame.dev.gymApp.features.auth.api.response;

import lombok.Builder;

@Builder
public record CookieResponse(
        String access, String refresh
) {
}
