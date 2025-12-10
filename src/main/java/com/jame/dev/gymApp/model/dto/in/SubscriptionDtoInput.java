package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SubscriptionDtoInput(
        @JsonProperty("customerId") @NotNull Long customerId,
        @JsonProperty("pricingId") @NotNull Integer pricingId
) {
}
