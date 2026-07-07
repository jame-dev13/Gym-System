package com.jame.dev.gymApp.features.metrics.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubsPerMembership(
        @JsonProperty("membership") String membership,
        @JsonProperty("subsCount") long subsCount
) {
}
