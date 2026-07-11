package com.jame.dev.gymApp.features.metrics.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerEvolution(
   @JsonProperty("month") String month,
   @JsonProperty("customersNum") long customersNum
) {
}
