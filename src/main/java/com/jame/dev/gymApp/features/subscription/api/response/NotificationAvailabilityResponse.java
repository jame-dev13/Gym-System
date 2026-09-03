package com.jame.dev.gymApp.features.subscription.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.subscription.infrastructure.notification.model.NotificationStatus;

public record NotificationAvailabilityResponse(
   @JsonProperty("availability") NotificationStatus status,
   @JsonProperty("ttl") Long ttl
) {
}
