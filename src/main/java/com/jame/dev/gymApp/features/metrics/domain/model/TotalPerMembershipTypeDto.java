package com.jame.dev.gymApp.features.metrics.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;

import java.math.BigDecimal;

public record TotalPerMembershipTypeDto(
        @JsonProperty("membership") Membership membership,
        @JsonProperty("total") BigDecimal total
) {
}
