package com.jame.dev.gymApp.model.listeners;

import com.jame.dev.gymApp.model.dto.in.UserDtoInput;

public record UserNotifiable(
        UserDtoInput input,
        boolean isNotifiable
) {
}
