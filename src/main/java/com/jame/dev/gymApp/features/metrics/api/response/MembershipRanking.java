package com.jame.dev.gymApp.features.metrics.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MembershipRanking(
   @JsonProperty("membership") String membership,
   @JsonProperty("subsCount") long subsCount,
   @JsonProperty("rank") long rank
) {
}
