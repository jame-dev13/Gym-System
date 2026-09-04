package com.jame.dev.gymApp.features.customer.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.customer.domain.model.CustomerAddressInfo;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CustomerUpdateRequest(
   @JsonProperty("phoneContact")
   @NotEmpty(message = "Field shouldn't be empty")
   @Pattern(regexp = "^[0-9]+$", message = "Should be only numeric characters.")
   String phoneContact,
   @JsonProperty("addressInfo")
   @NotNull(message = "Customer address info is required")
   CustomerAddressInfo customerAddressInfo
) {
}
