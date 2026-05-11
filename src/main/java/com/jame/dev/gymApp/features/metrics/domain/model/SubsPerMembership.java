package com.jame.dev.gymApp.features.metrics.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;

public record SubsPerMembership(
        @JsonProperty("membership") Membership membership,
        @JsonProperty("subsCount") long subsCount
) {
}
