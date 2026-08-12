package com.jame.dev.gymApp.features.customer.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record CustomerCurrentRequest(
   @JsonProperty("phoneContact")
   @NotEmpty(message = "Field shouldn't be empty")
   @Pattern(regexp = "^[0-9]+$", message = "Should be only numeric characters.")
   String phoneContact
) {
}
