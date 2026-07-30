package com.jame.dev.gymApp.features.notification.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.infrastructure.annotation.Minimum;
import jakarta.validation.constraints.Max;

public record SubscriberNotificationRequest(
   @JsonProperty("subscriptionId")
   @Minimum long subscriptionId,
   @JsonProperty("rangeDays")
   @Minimum
   @Max(value = 7, message = "Value must be min or equal than 7.") int rangeDays
) {
}
