package com.jame.dev.gymApp.model.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.shared.enums.Membership;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SubscriptionDtoOutput(
        @JsonProperty("id") Long id,
        @JsonProperty("customer") CustomerDtoOutput customer,
        @JsonProperty("membership") Membership membership,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("period") PeriodDtoOutput period,
        @JsonProperty("finished") Boolean finished
) {
}
