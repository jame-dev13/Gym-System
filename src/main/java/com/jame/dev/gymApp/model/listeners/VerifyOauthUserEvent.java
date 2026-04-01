package com.jame.dev.gymApp.model.listeners;

import com.jame.dev.gymApp.oauth2.model.AuthenticatedUser;

public record VerifyOauthUserEvent(AuthenticatedUser user) {
}
