package com.jame.dev.gymApp.model.dto.auth;

import com.jame.dev.gymApp.aspects.annotations.EmailValid;

public record ExtendExpirationRequest(
        @EmailValid
        String email
) {
}
