package com.jame.dev.gymApp.model.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jame.dev.gymApp.shared.enums.Membership;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

@Builder
@JsonSerialize
public record SubscriptionDtoOutput(
        @JsonProperty("id") @NonNull Long id,
        @JsonProperty("customerName") @NotBlank String customerName,
        @JsonProperty("membership") @NonNull Membership membership,
        @JsonProperty("price") @NonNull BigDecimal price,
        @JsonProperty("finished") @NonNull Boolean finished
        ) {
}
