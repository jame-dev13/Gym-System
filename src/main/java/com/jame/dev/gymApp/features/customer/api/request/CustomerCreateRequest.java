package com.jame.dev.gymApp.features.customer.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CustomerCreateRequest(
   @JsonProperty("userId")
   @Min(value = 1, message = "Unacceptable value provided.")
   @NotNull(message = "'userId' is required")
   Long userId
) {
}
