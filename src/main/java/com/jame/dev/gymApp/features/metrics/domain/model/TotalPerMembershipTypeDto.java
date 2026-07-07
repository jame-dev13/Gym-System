package com.jame.dev.gymApp.features.metrics.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record TotalPerMembershipTypeDto(
        @JsonProperty("membership") String membership,
        @JsonProperty("total") BigDecimal total
) {
}
