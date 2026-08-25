package com.jame.dev.gymApp.features.notification.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DayRangeRequest(
   @JsonProperty("numberOfDays")
   @NotNull(message = "Number of days is required.")
   @Min(value = 3, message = "Minimum acceptable value is 3.")
   @Max(value = 7, message = "Maximum acceptable value is 7.")
   int numberOfDays
) {
}
