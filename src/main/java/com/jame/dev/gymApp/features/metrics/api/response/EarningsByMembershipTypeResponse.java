package com.jame.dev.gymApp.features.metrics.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.metrics.domain.model.TotalPerMembershipTypeDto;

import java.util.List;

public record EarningsByMembershipTypeResponse(
   @JsonProperty("content") List<TotalPerMembershipTypeDto> content
) {
}
