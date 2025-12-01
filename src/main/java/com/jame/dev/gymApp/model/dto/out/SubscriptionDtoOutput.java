package com.jame.dev.gymApp.model.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.shared.enums.Membership;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record SubscriptionDtoOutput(
        @JsonProperty("id") Long id,
        @JsonProperty("customer") CustomerDtoOutput customer,
        @JsonProperty("membership") Membership membership,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("periods") List<PeriodDtoOutput> periods,
        @JsonProperty("finished") Boolean finished
) {
}
