package com.jame.dev.gymApp.model.dto.in;

import com.jame.dev.gymApp.aspects.annotations.EmailValid;

public record RecoveryRequest(
        @EmailValid String email
) {
}
