package com.jame.dev.gymApp.features.notification.domain.event;

import com.jame.dev.gymApp.features.notification.application.dto.NotifiableInfo;

import java.util.Set;

public record NotifyExpirationEvent(
   Set<NotifiableInfo> emailAdressSet
) {
}
