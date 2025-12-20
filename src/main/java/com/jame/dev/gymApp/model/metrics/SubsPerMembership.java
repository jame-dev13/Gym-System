package com.jame.dev.gymApp.model.metrics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.shared.enums.Membership;

public record SubsPerMembership(
        @JsonProperty("membership") Membership membership,
        @JsonProperty("subsCount") long subsCount
) {
}
