package com.jame.dev.gymApp.model.dto.auth;

import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;

public record ExtendExpirationRequest(
        @EmailValid
        String email
) {
}
