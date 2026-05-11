package com.jame.dev.gymApp.features.auth.domain.event;

import com.jame.dev.gymApp.features.user.api.request.UserRequest;

public record UserNotifiableEvent(
        UserRequest input,
        boolean isNotifiable
) {
}
