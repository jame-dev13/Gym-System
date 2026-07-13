package com.jame.dev.gymApp.features.metrics.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.metrics.domain.model.SubsPerMembership;

import java.util.List;

public record SubscriptionsPerMembershipResponse(
   @JsonProperty("content") List<SubsPerMembership> content
) {
}
