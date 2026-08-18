package com.jame.dev.gymApp.features.auth.domain.event;

import com.jame.dev.gymApp.features.auth.domain.model.CustomOAuth2User;

public record VerifyOauthUserEvent(CustomOAuth2User oAuth2User) {
}
