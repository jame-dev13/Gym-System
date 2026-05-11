package com.jame.dev.gymApp.features.auth.api.request;

import com.jame.dev.gymApp.infrastructure.annotation.EmailValid;

public record RecoveryRequest(
        @EmailValid String email
) {
}
