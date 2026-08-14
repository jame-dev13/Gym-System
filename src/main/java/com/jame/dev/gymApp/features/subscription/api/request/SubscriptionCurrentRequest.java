package com.jame.dev.gymApp.features.subscription.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jame.dev.gymApp.features.subscription.domain.model.Membership;

public record SubscriptionCurrentRequest(
   @JsonProperty("membership") Membership membership
) {
}
