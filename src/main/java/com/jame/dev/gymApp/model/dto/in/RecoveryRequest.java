package com.jame.dev.gymApp.model.dto.in;

import com.jame.dev.gymApp.aspects.annotations.constraints.EmailValid;

public record RecoveryRequest(
        @EmailValid String email
) {
}
