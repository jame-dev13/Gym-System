package com.jame.dev.gymApp.features.metrics.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.metrics.domain.model.CustomerEvolution;

import java.util.List;

public record CustomerEvolutionResponse(
   @JsonProperty("content") List<CustomerEvolution> content
   ) {
}
