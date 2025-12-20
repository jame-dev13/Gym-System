package com.jame.dev.gymApp.model.metrics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.shared.enums.Membership;

import java.math.BigDecimal;

public record TotalPerMembershipTypeDto(
        @JsonProperty("membership") Membership membership,
        @JsonProperty("total") BigDecimal total
) {
}
