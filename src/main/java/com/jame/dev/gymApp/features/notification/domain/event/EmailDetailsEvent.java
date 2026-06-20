package com.jame.dev.gymApp.features.notification.domain.event;

import com.jame.dev.gymApp.features.notification.application.dto.EmailDetails;

public record EmailDetailsEvent(
   EmailDetails emailDetails
) {
}
