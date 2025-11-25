package com.jame.dev.gymApp.model.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;

@Builder
@JsonDeserialize
public record SubscriptionDtoInput(
        @JsonProperty("customerId") @NotNull Long customerId,
        @JsonProperty("pricingId") @NotNull Integer pricingId,
        @JsonProperty("periodId") @NonNull Long periodId,
        @JsonProperty("subscriptionDateId") @NonNull Long subscriptionDateId,
        @JsonProperty("active") @NonNull Boolean active,
        @JsonProperty("finished") @NonNull Boolean finished
) {
   public SubscriptionDtoInput{
      active = Boolean.TRUE;
   }
}
