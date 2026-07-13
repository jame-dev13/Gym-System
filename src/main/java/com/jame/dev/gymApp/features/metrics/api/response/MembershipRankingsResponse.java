package com.jame.dev.gymApp.features.metrics.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MembershipRankingsResponse(
   @JsonProperty("content") List<MembershipRanking> content
) {
}
