package com.jame.dev.gymApp.model.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jame.dev.gymApp.shared.enums.Membership;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

@Builder
@JsonSerialize
public record PricingDtoOutput(
        @JsonProperty("id") @NonNull Integer id,
        @JsonProperty("membership") @NonNull Membership membership,
        @JsonProperty("price") @NonNull BigDecimal price
        ) {
}
