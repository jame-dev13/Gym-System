package com.jame.dev.gymApp.features.notification.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record SubscriberNotificationResponse(
   @JsonProperty("uuid") UUID uuid,
   @JsonProperty("rangeDaysNotification") int rangeDaysNotification,
   @JsonProperty("nextNotificationDate") String nextNotificationDate,
   @JsonProperty("lastNotificationDate") String lastNotificationDate
) {
}
