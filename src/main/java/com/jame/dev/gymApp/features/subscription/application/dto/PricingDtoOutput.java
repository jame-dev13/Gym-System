package com.jame.dev.gymApp.features.subscription.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PricingDtoOutput(
        @JsonProperty("id") Integer id,
        @JsonProperty("membership") Membership membership,
        @JsonProperty("price") BigDecimal price
) {
}
