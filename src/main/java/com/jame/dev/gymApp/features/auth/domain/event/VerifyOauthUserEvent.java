package com.jame.dev.gymApp.features.auth.domain.event;

import com.jame.dev.gymApp.features.auth.domain.model.AuthenticatedUser;

public record VerifyOauthUserEvent(AuthenticatedUser user) {
}
