package com.jame.dev.gymApp.features.metrics.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.metrics.domain.model.SubscriberEvolution;

import java.util.List;

public record SubscriberEvolutionResponse(
   @JsonProperty("content") List<SubscriberEvolution> content
) {
}
