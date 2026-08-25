package com.jame.dev.gymApp.features.notification.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubscriberNotificationResponse(
   @JsonProperty("rangeDaysNotification") int rangeDaysNotification,
   @JsonProperty("nextNotificationDate") String nextNotificationDate,
   @JsonProperty("notifiable") boolean notifiable
) {
}
